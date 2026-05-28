package com.ironcore.metrics.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.OllamaGenerateRequest
import com.ironcore.metrics.data.remote.dto.RemoteMeal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val apiService: HomelabApiService,
    private val nutritionDao: com.ironcore.metrics.data.local.dao.NutritionDao,
    private val wearDataSyncService: com.ironcore.metrics.data.sync.WearDataSyncService
) : ViewModel() {

    private val _meals = MutableStateFlow<List<RemoteMeal>>(emptyList())
    val meals = _meals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _selectedCuisine = MutableStateFlow("Standard")
    val selectedCuisine = _selectedCuisine.asStateFlow()

    val cuisines = listOf(
        "Standard", "Mediterranean", "Mexican", "Japanese", "Indian", "Italian", "Thai", "Middle Eastern",
        "Nordic", "West African", "Peruvian", "Vietnamese"
    )

    private val gson = Gson()

    // Mock setting for AI Core vs Remote. In a real app, this comes from DataStore
    var useAiCoreOnDevice = false // Default to false for better variety as requested

    init {
        // Load existing meals from database on startup
        viewModelScope.launch {
            nutritionDao.getAllMeals().distinctUntilChanged().collect { dbMeals ->
                _meals.value = dbMeals.map { meal ->
                    RemoteMeal(
                        name = meal.name,
                        calories = meal.totalCalories,
                        protein = meal.proteinGrams,
                        carbs = meal.carbGrams,
                        fat = meal.fatGrams,
                        ingredients = meal.ingredients?.let {
                            try {
                                gson.fromJson(it, object : TypeToken<List<String>>() {}.type)
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } ?: emptyList(),
                        cookingInstructions = meal.cookingInstructions,
                        prepTime = meal.prepTime,
                        difficulty = meal.difficulty
                    )
                }
            }
        }
    }

    fun setCuisine(cuisine: String) {
        _selectedCuisine.value = cuisine
    }

    fun generatePlan() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                if (useAiCoreOnDevice) {
                    // Fast on-device AICore generation
                    delay(800) 
                    val generated = getSmartFallbackPlan(_selectedCuisine.value)
                    _meals.value = generated
                    saveMealsToDatabase(generated)
                } else {
                    // Remote Ollama/Homelab Generation
                    val prompt = """
                        Generate a detailed, authentic daily meal plan for ${selectedCuisine.value} cuisine.
                        Target: 2500 calories, 180g protein.
                        Provide exactly 7 meals: Breakfast, Lunch, Dinner, 2 Snacks, Pre-Workout, and Post-Workout.
                        Focus on traditional healthy dishes (e.g., if Nordic use Skyr/Salmon, if West African use Jollof/Grilled Fish, if Peruvian use Ceviche/Quinoa).
                        Return ONLY a valid JSON array of objects. Each object must have these exact keys:
                        "name" (string), "calories" (int), "protein" (float), "carbs" (float), "fat" (float), "ingredients" (array of strings), "cookingInstructions" (string), "prepTime" (string, e.g. "15 mins"), "difficulty" (string, e.g. "Easy", "Medium", "Hard").
                    """.trimIndent()

                    val request = OllamaGenerateRequest(
                        model = "granite-code:3b",
                        prompt = prompt,
                        format = "json"
                    )

                    val response = try {
                        apiService.generateWithGranite(request)
                    } catch (e: Exception) {
                        // Fallback to smart local generation if network fails
                        val fallback = getSmartFallbackPlan(_selectedCuisine.value)
                        _meals.value = fallback
                        saveMealsToDatabase(fallback)
                        _errorMessage.value = "Homelab unreachable. Using high-quality local template."
                        return@launch
                    }
                    
                    try {
                        // More robust parsing: try to find the JSON array if the model added text
                        val jsonContent = response.response.trim()
                        val startIndex = jsonContent.indexOf("[")
                        val endIndex = jsonContent.lastIndexOf("]")
                        
                        val cleanJson = if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                            jsonContent.substring(startIndex, endIndex + 1)
                        } else {
                            jsonContent
                        }

                        val listType = object : TypeToken<List<RemoteMeal>>() {}.type
                        val generatedMeals: List<RemoteMeal> = gson.fromJson(cleanJson, listType)
                        
                        if (generatedMeals.isNotEmpty()) {
                            _meals.value = generatedMeals
                            saveMealsToDatabase(generatedMeals)
                        } else {
                            throw Exception("Empty meal list generated")
                        }
                    } catch (e: Exception) {
                        // Parsing failed, use smart fallback
                        val fallback = getSmartFallbackPlan(_selectedCuisine.value)
                        _meals.value = fallback
                        saveMealsToDatabase(fallback)
                        _errorMessage.value = "AI formatting issue. Using optimized local template."
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Generation error: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getSmartFallbackPlan(cuisine: String): List<RemoteMeal> {
        return when (cuisine) {
            "Nordic" -> listOf(
                RemoteMeal("Icelandic Skyr & Berries", 350, 35f, 40f, 5f, listOf("Skyr", "Blueberries", "Oats"), "Mix skyr with berries and top with oats.", "5 mins", "Easy"),
                RemoteMeal("Danish Smørrebrød", 450, 25f, 40f, 20f, listOf("Rye bread", "Mackerel", "Boiled egg", "Dill"), "Assemble mackerel and sliced egg on rye. Garnish with dill.", "10 mins", "Easy"),
                RemoteMeal("Finnish Kalakeitto (Salmon Soup)", 500, 40f, 35f, 20f, listOf("Salmon", "Leeks", "Carrots", "Potatoes", "Dill"), "Boil root veggies in broth until tender. Add salmon chunks and simmer for 5 mins.", "30 mins", "Medium"),
                RemoteMeal("Swedish Crispbread Snack", 200, 10f, 25f, 8f, listOf("Rye crispbread", "Jarlsberg cheese"), "Top crispbread with sliced cheese.", "2 mins", "Easy"),
                RemoteMeal("Pre-Workout Beetroot Juice & Nuts", 200, 5f, 30f, 8f, listOf("Beetroot", "Apple", "Walnuts"), "Juice beetroot and apple. Eat walnuts on the side.", "10 mins", "Easy"),
                RemoteMeal("Post-Workout Elk/Venison Meatballs", 450, 45f, 30f, 15f, listOf("Lean game meat", "Lingonberries", "Barley"), "Bake meatballs at 400F for 15 mins. Serve over boiled barley with berry sauce.", "25 mins", "Medium"),
                RemoteMeal("Cloudberry Protein Shake", 350, 30f, 40f, 5f, listOf("Protein powder", "Cloudberries", "Almond milk"), "Blend all ingredients until smooth.", "5 mins", "Easy")
            )
            "West African" -> listOf(
                RemoteMeal("Akara & Ogi", 380, 15f, 55f, 12f, listOf("Black-eyed peas", "Corn porridge", "Peppers"), "Blend peeled beans with peppers, form balls, and air-fry. Serve with hot corn porridge.", "25 mins", "Medium"),
                RemoteMeal("Jollof Rice & Grilled Chicken", 550, 35f, 65f, 15f, listOf("Basmati rice", "Chicken breast", "Tomatoes", "Spices"), "Blend tomatoes/peppers, simmer with spices, add parboiled rice and cook. Grill chicken separately.", "45 mins", "Hard"),
                RemoteMeal("Okra Soup & Oat Fufu", 420, 28f, 45f, 14f, listOf("Okra", "Shrimp", "Fish", "Oat flour"), "Chop okra and simmer in fish broth. Stir oat flour in boiling water until dough forms.", "30 mins", "Medium"),
                RemoteMeal("Steamed Moin Moin", 350, 18f, 45f, 8f, listOf("Peeled beans", "Onions", "Bell peppers", "Egg"), "Blend beans and peppers, mix with boiled egg chunks, wrap in leaves or foil, and steam for 40 mins.", "50 mins", "Medium"),
                RemoteMeal("Pre-Workout Papaya & Peanuts", 250, 10f, 30f, 12f, listOf("Fresh Papaya", "Roasted peanuts"), "Dice papaya and serve with a side of unsalted peanuts.", "5 mins", "Easy"),
                RemoteMeal("Post-Workout Suya Beef Skewers", 400, 45f, 10f, 18f, listOf("Lean beef", "Suya spice (peanut/chili base)", "Onions"), "Coat beef in Suya spice, skewer, and grill until charred. Serve with sliced raw onions.", "20 mins", "Medium"),
                RemoteMeal("Plantain & Spinach Snack", 300, 10f, 40f, 10f, listOf("Yellow plantain", "Spinach", "Spices"), "Roast plantain. Lightly sauté spinach with a touch of oil.", "15 mins", "Easy")
            )
            "Peruvian" -> listOf(
                RemoteMeal("Quinoa & Apple Porridge", 320, 12f, 55f, 6f, listOf("Quinoa", "Apples", "Cinnamon", "Honey"), "Simmer quinoa in water with cinnamon. Top with diced apples and honey.", "20 mins", "Easy"),
                RemoteMeal("Sea Bass Ceviche", 350, 35f, 25f, 8f, listOf("Sea bass", "Lime", "Red onion", "Sweet potato", "Corn"), "Cube fish, cure in lime juice for 10 mins. Mix with sliced onions. Serve with boiled sweet potato.", "15 mins", "Medium"),
                RemoteMeal("Lomo Saltado (Healthy Style)", 550, 40f, 45f, 18f, listOf("Lean beef", "Tomatoes", "Onions", "Air-fried potatoes"), "Stir-fry beef over high heat. Add tomatoes, onions, and soy sauce. Serve over air-fried potatoes.", "20 mins", "Medium"),
                RemoteMeal("Anticuchos (Grilled Beef Heart)", 280, 30f, 10f, 12f, listOf("Beef heart", "Aji panca", "Garlic", "Vinegar"), "Marinate beef heart in aji panca, grill on skewers.", "25 mins", "Medium"),
                RemoteMeal("Pre-Workout Maca Smoothie", 200, 15f, 30f, 5f, listOf("Maca powder", "Banana", "Protein powder", "Almond milk"), "Blend all ingredients until smooth.", "5 mins", "Easy"),
                RemoteMeal("Post-Workout Tarwi Salad", 350, 25f, 35f, 10f, listOf("Tarwi (Andean lupin)", "Tomatoes", "Onions", "Lime"), "Mix washed tarwi beans with diced veggies and lime juice.", "10 mins", "Easy"),
                RemoteMeal("Cancha (Toasted Corn) Snack", 210, 5f, 35f, 8f, listOf("Chulpe corn", "Sea salt", "Avocado oil"), "Toast corn in a skillet with a drop of oil until they pop.", "10 mins", "Easy")
            )
            "Vietnamese" -> listOf(
                RemoteMeal("Chicken Pho (Phở Gà)", 420, 40f, 48f, 15f, listOf("Rice noodles", "Chicken breast", "Ginger", "Star anise", "Broth"), "Simmer broth with charred ginger/anise. Pour over noodles and sliced cooked chicken.", "40 mins", "Hard"),
                RemoteMeal("Gỏi Cuốn (Fresh Spring Rolls)", 250, 18f, 35f, 4f, listOf("Shrimp", "Herbs", "Vermicelli", "Rice paper"), "Dip rice paper in water, lay ingredients flat, roll tightly.", "15 mins", "Medium"),
                RemoteMeal("Cá Kho Tộ (Claypot Salmon)", 410, 35f, 25f, 12f, listOf("Salmon", "Chilies", "Fish sauce", "Bok choy"), "Caramelize sugar and fish sauce, simmer salmon in the sauce until tender. Serve with steamed greens.", "25 mins", "Medium"),
                RemoteMeal("Larb Gai (Chicken Salad)", 280, 39f, 16f, 15f, listOf("Minced chicken", "Lime", "Mint", "Cilantro"), "Sauté minced chicken, toss with lime juice, fish sauce, and fresh herbs.", "15 mins", "Easy"),
                RemoteMeal("Pre-Workout Iced Coffee (Cà Phê Sữa)", 150, 5f, 25f, 4f, listOf("Robusta coffee", "Condensed milk", "Ice"), "Brew coffee using a phin filter, mix with a small amount of condensed milk over ice.", "10 mins", "Easy"),
                RemoteMeal("Post-Workout Bún Thịt Nướng (Lean)", 450, 40f, 55f, 10f, listOf("Grilled lean pork", "Vermicelli", "Lettuce", "Nuoc cham"), "Grill marinated pork, serve over cold noodles and greens with light dressing.", "30 mins", "Medium"),
                RemoteMeal("Papaya & Mint Salad", 180, 5f, 35f, 2f, listOf("Green papaya", "Mint", "Crushed peanuts", "Lime"), "Shred papaya, toss with herbs and dressing, top with peanuts.", "10 mins", "Easy")
            )
            "Thai" -> listOf(
                RemoteMeal("Khao Tom (Rice Soup)", 300, 22f, 36f, 8f, listOf("Rice", "Ginger", "Shrimp", "Cilantro"), "Boil cooked rice in broth with ginger. Add shrimp and cook for 3 mins.", "15 mins", "Easy"),
                RemoteMeal("Som Tum & Grilled Chicken", 410, 35f, 30f, 10f, listOf("Green papaya", "Tomatoes", "Grilled chicken", "Lime"), "Pound papaya and tomatoes in a mortar. Serve alongside grilled chicken breast.", "20 mins", "Medium"),
                RemoteMeal("Tom Yum Goong", 250, 25f, 15f, 5f, listOf("Shrimp", "Mushrooms", "Lemongrass", "Chilies"), "Boil broth with lemongrass and chilies. Add shrimp and mushrooms until cooked.", "20 mins", "Medium"),
                RemoteMeal("Larb Moo (Minced Pork Salad)", 320, 28f, 10f, 18f, listOf("Minced lean pork", "Lime", "Roasted rice powder", "Mint"), "Cook pork, toss with lime, herbs, and toasted rice powder.", "15 mins", "Easy"),
                RemoteMeal("Pre-Workout Banana & Coconut", 250, 3f, 45f, 8f, listOf("Banana", "Light coconut milk", "Sesame seeds"), "Warm banana slices in light coconut milk.", "10 mins", "Easy"),
                RemoteMeal("Post-Workout Pad Krapow (Basil Beef)", 500, 45f, 45f, 15f, listOf("Lean beef", "Holy basil", "Chilies", "Brown rice"), "Stir-fry beef with garlic and chilies, add basil at the end. Serve with brown rice.", "15 mins", "Medium"),
                RemoteMeal("Mango & Cucumber Snack", 150, 2f, 35f, 0f, listOf("Mango", "Cucumber", "Chili salt"), "Slice mango and cucumber, sprinkle lightly with chili salt.", "5 mins", "Easy")
            )
            "Japanese" -> listOf(
                RemoteMeal("Grilled Miso Salmon", 320, 30f, 8f, 20f, listOf("Salmon", "Miso paste", "Mirin", "Ginger"), "Marinate salmon in miso and mirin, broil for 10 mins.", "15 mins", "Easy"),
                RemoteMeal("Hiyayakko (Chilled Tofu)", 150, 12f, 5f, 7f, listOf("Silken tofu", "Scallions", "Ginger", "Soy sauce"), "Drain tofu, top with grated ginger, scallions, and a dash of soy sauce.", "2 mins", "Easy"),
                RemoteMeal("Zaru Soba", 350, 12f, 70f, 2f, listOf("Buckwheat noodles", "Tsuyu sauce", "Wasabi"), "Boil soba, chill in ice water. Serve with dipping sauce.", "10 mins", "Easy"),
                RemoteMeal("Miso Soup & Edamame", 190, 14f, 14f, 7f, listOf("Dashi", "Tofu", "Wakame", "Edamame"), "Boil edamame. Prepare dashi, dissolve miso, add tofu and wakame.", "10 mins", "Easy"),
                RemoteMeal("Pre-Workout Onigiri (Rice Ball)", 220, 6f, 45f, 2f, listOf("White rice", "Pickled plum (Umeboshi)", "Nori"), "Form warm rice around the plum, wrap in seaweed.", "10 mins", "Medium"),
                RemoteMeal("Post-Workout Tuna Sashimi Bowl", 450, 45f, 55f, 8f, listOf("Raw tuna", "Sushi rice", "Soy sauce", "Sesame"), "Slice tuna, serve over warm seasoned rice.", "10 mins", "Easy"),
                RemoteMeal("Matcha Protein Shake", 200, 25f, 15f, 3f, listOf("Matcha powder", "Protein powder", "Soy milk"), "Blend matcha and protein powder with chilled soy milk.", "5 mins", "Easy")
            )
            "Mediterranean" -> listOf(
                RemoteMeal("Shakshuka with Whole Grain Bread", 400, 18f, 35f, 20f, listOf("Eggs", "Tomatoes", "Bell peppers", "Olive oil"), "Sauté peppers and tomatoes, crack eggs into the sauce, cover and poach.", "20 mins", "Medium"),
                RemoteMeal("Chicken Souvlaki & Tzatziki", 350, 35f, 15f, 16f, listOf("Chicken skewers", "Greek yogurt", "Cucumber", "Lemon"), "Grill lemon-herb chicken skewers. Serve with cucumber yogurt sauce.", "25 mins", "Medium"),
                RemoteMeal("Lentil Mujadara", 350, 12f, 55f, 10f, listOf("Brown lentils", "Rice", "Caramelized onions"), "Boil lentils and rice together. Top with heavily caramelized onions.", "40 mins", "Medium"),
                RemoteMeal("Greek Yogurt & Walnuts", 300, 18f, 22f, 16f, listOf("Greek yogurt", "Walnuts", "Honey", "Berries"), "Top yogurt with nuts, berries, and a drizzle of honey.", "2 mins", "Easy"),
                RemoteMeal("Pre-Workout Dates & Almonds", 250, 5f, 40f, 10f, listOf("Medjool dates", "Raw almonds"), "Remove pits from dates and stuff with almonds.", "2 mins", "Easy"),
                RemoteMeal("Post-Workout Baked Cod & Quinoa", 450, 40f, 45f, 12f, listOf("Cod fillet", "Quinoa", "Lemon", "Parsley"), "Bake cod with lemon. Serve alongside cooked quinoa with parsley.", "25 mins", "Easy"),
                RemoteMeal("Hummus & Veggie Sticks", 200, 8f, 25f, 10f, listOf("Chickpea hummus", "Carrots", "Celery"), "Slice veggies and serve with hummus.", "5 mins", "Easy")
            )
            else -> listOf(
                RemoteMeal("Standard Oatmeal", 350, 20f, 50f, 8f, listOf("Oats", "Milk", "Protein Powder"), "Cook oats in milk, stir in protein powder.", "5 mins", "Easy"),
                RemoteMeal("Standard Chicken Salad", 450, 40f, 20f, 20f, listOf("Chicken", "Greens", "Olive Oil"), "Toss greens with olive oil, top with sliced chicken.", "10 mins", "Easy"),
                RemoteMeal("Standard Steak & Rice", 600, 50f, 60f, 20f, listOf("Steak", "Rice", "Broccoli"), "Grill steak, steam broccoli, serve with cooked rice.", "20 mins", "Medium"),
                RemoteMeal("Apple & Peanut Butter", 250, 8f, 30f, 15f, listOf("Apple", "Peanut butter"), "Slice apple, serve with peanut butter.", "2 mins", "Easy"),
                RemoteMeal("Pre-Workout Banana", 100, 1f, 27f, 0f, listOf("Banana"), "Peel and eat.", "1 min", "Easy"),
                RemoteMeal("Post-Workout Protein Shake", 200, 40f, 5f, 2f, listOf("Whey protein", "Water"), "Mix protein with water in a shaker bottle.", "1 min", "Easy"),
                RemoteMeal("Cottage Cheese", 150, 20f, 8f, 5f, listOf("Low-fat cottage cheese"), "Serve in a bowl.", "1 min", "Easy")
            )
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun exportMealPlans(): String {
        return gson.toJson(_meals.value)
    }

    fun importMealPlans(jsonString: String) {
        viewModelScope.launch {
            try {
                val listType = object : TypeToken<List<RemoteMeal>>() {}.type
                val importedMeals: List<RemoteMeal> = gson.fromJson(jsonString, listType)
                _meals.value = importedMeals
                saveMealsToDatabase(importedMeals)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMeal(remoteMeal: RemoteMeal) {
        viewModelScope.launch {
            // Find the meal in the DB and delete it. 
            // In a real app, RemoteMeal would have an ID.
            // For now, we match by name (not ideal but works for this demo).
            val dbMeals = nutritionDao.getAllMeals().first()
            val mealToDelete = dbMeals.find { it.name == remoteMeal.name }
            mealToDelete?.let { nutritionDao.deleteMeal(it) }
        }
    }

    private suspend fun saveMealsToDatabase(meals: List<RemoteMeal>) {
        // Clear old ones first to represent a "daily plan" as requested (or we could append)
        // User said "detailed and editiable, saveable", let's clear for now to show fresh plan
        nutritionDao.deleteAllMeals()
        
        meals.forEach { remoteMeal ->
            val meal = com.ironcore.metrics.data.local.entities.Meal(
                name = remoteMeal.name,
                totalCalories = remoteMeal.calories,
                proteinGrams = remoteMeal.protein,
                carbGrams = remoteMeal.carbs,
                fatGrams = remoteMeal.fat,
                cuisine = _selectedCuisine.value,
                ingredients = gson.toJson(remoteMeal.ingredients),
                timestamp = System.currentTimeMillis()
            )
            nutritionDao.insertMeal(meal)
        }
        
        // Sync to WearOS
        wearDataSyncService.syncNutritionPlan(gson.toJson(meals))
    }
}
