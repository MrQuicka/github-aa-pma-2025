# FitTrack - Příprava na obhajobu semestrálního projektu

## Obsah
1. [Přehled projektu](#přehled-projektu)
2. [Architektura aplikace](#architektura-aplikace)
3. [Klíčové technologie](#klíčové-technologie)
4. [Databázová vrstva](#databázová-vrstva)
5. [CRUD operace](#crud-operace)
6. [Kritické části kódu](#kritické-části-kódu)
7. [Lifecycle & Threading](#lifecycle--threading)
8. [UI komponenty](#ui-komponenty)
9. [Možné otázky na obhajobu](#možné-otázky-na-obhajobu)
10. [Demo scénář](#demo-scénář)
11. [Checklist před obhajobou](#checklist-před-obhajobou)

---

## Přehled projektu

**FitTrack** je Android aplikace pro sledování fitness aktivit s následujícími funkcemi:

### Povinné požadavky (splněno)
- ✅ **Databáze**: Room Database s 3 entitami (User, Workout, Meal)
- ✅ **CRUD operace**: Kompletní Create, Read, Update, Delete pro všechny entity
- ✅ **Úvodní nastavení**: První spuštění s formulářem (pohlaví, výška, váha, věk)
- ✅ **BMR kalkulátor**: Harris-Benedict vzorec (revize 1984)
- ✅ **Dashboard**: Denní přehled s progressem

### Bonus funkcionalita
- ✅ Denní tracking kalorií (BMR - přijaté kalorie)
- ✅ Motivační prvek pro denní cvičení
- ✅ Uživatelský profil s možností editace
- ✅ Komplexní statistiky (celkové + dnešní)
- ✅ Material Design 3 UI

---

## Architektura aplikace

### Použitý pattern: **Repository Pattern** (částečně MVVM)

```
┌─────────────────────────────────────────┐
│           UI Layer (Fragments)          │
│  DashboardFragment, StatsFragment,      │
│  HistoryFragment                        │
└─────────────────┬───────────────────────┘
                  │ observe LiveData
┌─────────────────▼───────────────────────┐
│         Repository Layer                │
│      FitTrackRepository                 │
│  (kombinuje data z DAO)                 │
└─────────────────┬───────────────────────┘
                  │ volá metody
┌─────────────────▼───────────────────────┐
│          Data Layer (Room)              │
│  UserDao, WorkoutDao, MealDao           │
└─────────────────┬───────────────────────┘
                  │ přístup k
┌─────────────────▼───────────────────────┐
│         Database Layer                  │
│      FitTrackDatabase (SQLite)          │
│  User, Workout, Meal tabulky            │
└─────────────────────────────────────────┘
```

### Proč tento pattern?

**Repository Pattern** centralizuje přístup k datům:
- ✅ Odděluje business logiku od databázových operací
- ✅ Usnadňuje testování (mock repository)
- ✅ Snadno se přidávají nové zdroje dat (např. API)
- ✅ Jediné místo pro kombinaci více datových zdrojů

---

## Klíčové technologie

### 1. Room Database

**Co to je?**
- Android abstrakce nad SQLite
- Compile-time ověřování SQL dotazů
- Automatická konverze mezi objekty a databází

**Proč Room a ne SQLite přímo?**
| Room | SQLite přímo |
|------|--------------|
| Type-safe dotazy | String SQL dotazy (chybné až za běhu) |
| Automatická konverze | Manuální parsing Cursor |
| LiveData integrace | Manuální observery |
| Méně boilerplate kódu | Hodně boilerplate kódu |

**Soubory:**
- [FitTrackDatabase.kt](app/src/main/java/com/example/myapp011fittrack/data/FitTrackDatabase.kt) - Hlavní databáze
- [User.kt](app/src/main/java/com/example/myapp011fittrack/data/User.kt) - Entity pro uživatele
- [Workout.kt](app/src/main/java/com/example/myapp011fittrack/data/Workout.kt) - Entity pro tréninky
- [Meal.kt](app/src/main/java/com/example/myapp011fittrack/data/Meal.kt) - Entity pro jídla

### 2. LiveData

**Co to je?**
- Lifecycle-aware observable data holder
- Automaticky aktualizuje UI při změně dat
- Respektuje lifecycle (žádné crashes při rotaci)

**Proč LiveData?**
```kotlin
// Bez LiveData - manuální update UI
fun loadData() {
    database.getUser { user ->
        runOnUiThread {
            textView.text = user.name
        }
    }
}

// S LiveData - automatická aktualizace
repository.user.observe(viewLifecycleOwner) { user ->
    textView.text = user.name  // automaticky při změně
}
```

### 3. Kotlin Coroutines

**Co to jsou?**
- Asynchronní programování bez callback hell
- Lehké thread-like struktury
- Integrace s Room & Lifecycle

**Proč Coroutines?**
```kotlin
// Bez Coroutines - callback hell
Thread {
    val user = database.getUser()
    runOnUiThread {
        updateUI(user)
    }
}.start()

// S Coroutines - čitelný kód
lifecycleScope.launch {
    val user = database.getUser()  // pozadí automaticky
    updateUI(user)  // UI thread automaticky
}
```

### 4. Sealed Class

**Co to je?**
- "Enum on steroids"
- Může mít data navíc u každého typu
- Type-safe when statements

**Proč Sealed Class pro Activity?**

```kotlin
// Activity.kt - sealed class
sealed class Activity {
    data class WorkoutActivity(val workout: Workout) : Activity()
    data class MealActivity(val meal: Meal) : Activity()
}

// Použití - compiler ví o všech typech
when (activity) {
    is Activity.WorkoutActivity -> show(activity.workout)
    is Activity.MealActivity -> show(activity.meal)
    // žádný else potřeba - compiler ví, že to jsou všechny případy
}
```

**Srovnání s Enum:**

| Sealed Class | Enum |
|--------------|------|
| Může mít různá data | Všechny instance mají stejnou strukturu |
| Type-safe | Type-safe |
| Může mít metody | Může mít metody |
| Lepší pro komplexní state | Lepší pro jednoduché konstanty |

---

## Databázová vrstva

### Entity

#### User (User.kt:5-26)
```kotlin
@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: Int = 1,  // Pouze jeden uživatel
    val gender: String,  // "male" nebo "female"
    val heightCm: Double,
    val weightKg: Double,
    val ageYears: Int,
    val bmr: Double = 0.0
) {
    // Harris-Benedict formula (1984 revision)
    fun calculateBMR(): Double {
        return if (gender == "male") {
            88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * ageYears)
        } else {
            447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * ageYears)
        }
    }

    fun withCalculatedBMR(): User {
        return this.copy(bmr = calculateBMR())
    }
}
```

**Klíčové body:**
- `@Entity` - označuje Room tabulku
- `@PrimaryKey` - primární klíč (vždy id=1, jen jeden uživatel)
- `calculateBMR()` - Harris-Benedict vzorec (1984)
- `withCalculatedBMR()` - immutable update (Kotlin best practice)

#### Workout (Workout.kt:5-14)
```kotlin
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationMinutes: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val caloriesBurned: Double = 0.0
)
```

**Klíčové body:**
- `autoGenerate = true` - automatické ID
- `timestamp` - default aktuální čas

#### Meal (Meal.kt:5-13)
```kotlin
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
```

### DAO (Data Access Objects)

#### UserDao (UserDao.kt)
```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser(): LiveData<User?>

    @Query("SELECT * FROM user WHERE id = 1")
    suspend fun getUserSync(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}
```

**Klíčové body:**
- `@Dao` - označuje DAO interface
- `@Query` - SQL dotazy (ověřeno za compile time!)
- `suspend` - coroutine funkce (background thread)
- `LiveData` verze - automatická observace změn
- `OnConflictStrategy.REPLACE` - update při duplicitě

#### WorkoutDao (WorkoutDao.kt)
```kotlin
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    fun getAllWorkouts(): LiveData<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long): Workout?

    @Insert
    suspend fun insertWorkout(workout: Workout)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}
```

### FitTrackDatabase (FitTrackDatabase.kt:10-35)

```kotlin
@Database(
    entities = [User::class, Workout::class, Meal::class],
    version = 1,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

**Klíčové body:**
- **Singleton pattern** - pouze jedna instance databáze
- `@Volatile` - thread-safe viditelnost
- `synchronized(this)` - thread-safe inicializace
- `context.applicationContext` - prevence memory leak

**Proč Singleton?**
- Room databáze je drahá na vytvoření
- Chceme sdílet jednu instanci napříč celou aplikací
- Thread-safe přístup

### FitTrackRepository (FitTrackRepository.kt:9-42)

```kotlin
class FitTrackRepository(
    private val userDao: UserDao,
    private val workoutDao: WorkoutDao,
    private val mealDao: MealDao
) {
    val user: LiveData<User?> = userDao.getUser()
    val allWorkouts: LiveData<List<Workout>> = workoutDao.getAllWorkouts()
    val allMeals: LiveData<List<Meal>> = mealDao.getAllMeals()

    // Kombinovaný seznam všech aktivit
    val allActivities: LiveData<List<Activity>> = MediatorLiveData<List<Activity>>().apply {
        var workouts: List<Workout> = emptyList()
        var meals: List<Meal> = emptyList()

        fun update() {
            val activities = mutableListOf<Activity>()
            activities.addAll(workouts.map { Activity.WorkoutActivity(it.id, it) })
            activities.addAll(meals.map { Activity.MealActivity(it.id, it) })
            value = activities.sortedByDescending { it.timestamp }
        }

        addSource(allWorkouts) {
            workouts = it
            update()
        }

        addSource(allMeals) {
            meals = it
            update()
        }
    }

    suspend fun insertWorkout(workout: Workout) = workoutDao.insertWorkout(workout)
    suspend fun updateWorkout(workout: Workout) = workoutDao.updateWorkout(workout)
    suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)
    // ... další metody
}
```

**Klíčové body:**
- **MediatorLiveData** - kombinuje více LiveData zdrojů
- `addSource` - reaguje na změny z více DAO
- `sortedByDescending` - nejnovější aktivita nahoře
- Centralizovaný přístup k datům

**Proč MediatorLiveData?**
- Chceme zobrazit workouts + meals v jednom seznamu
- Automatická aktualizace při změně workoutů NEBO meals
- UI se aktualizuje automaticky

---

## CRUD operace

### CREATE (Vytvoření)

**Příklad: Vytvoření nového tréninku**

```kotlin
// WorkoutDetailActivity.kt:108-119
private fun saveWorkout() {
    lifecycleScope.launch {
        val workout = Workout(
            name = workoutName,
            durationMinutes = durationMinutes,
            notes = workoutNotes
        )
        database.workoutDao().insertWorkout(workout)
        Toast.makeText(this@WorkoutDetailActivity, "Cvičení uloženo", Toast.LENGTH_SHORT).show()
        finish()
    }
}
```

**Co se děje?**
1. `lifecycleScope.launch` - spustí coroutine (background thread)
2. Vytvoří nový `Workout` objekt
3. `insertWorkout` - Room automaticky uloží do SQLite
4. `finish()` - zavře aktivitu

**Proč lifecycleScope?**
- Automaticky zruší coroutine při destroy aktivity
- Prevence memory leaks

### READ (Čtení)

**Příklad: Načtení všech tréninků**

```kotlin
// StatsFragment.kt:68-72
private fun observeData() {
    repository.allWorkouts.observe(viewLifecycleOwner) { workouts ->
        updateWorkoutStats(workouts)
    }
}
```

**Co se děje?**
1. `repository.allWorkouts` - LiveData s tréninky
2. `observe(viewLifecycleOwner)` - automatická observace změn
3. Při změně dat se volá lambda s novými daty
4. `updateWorkoutStats` - aktualizuje UI

**Proč viewLifecycleOwner?**
- Ve fragmentu NIKDY nepoužívat `this` pro observe!
- `viewLifecycleOwner` odpovídá lifecycle view fragmentu
- Prevence crashes a memory leaks

### UPDATE (Aktualizace)

**Příklad: Editace tréninku**

```kotlin
// WorkoutDetailActivity.kt:121-135
private fun updateWorkout() {
    lifecycleScope.launch {
        val existingWorkout = database.workoutDao().getWorkoutById(workoutId!!)
        if (existingWorkout != null) {
            val updatedWorkout = existingWorkout.copy(
                name = workoutName,
                durationMinutes = durationMinutes,
                notes = workoutNotes
            )
            database.workoutDao().updateWorkout(updatedWorkout)
            Toast.makeText(this@WorkoutDetailActivity, "Cvičení aktualizováno", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
```

**Co se děje?**
1. Načte existující workout podle ID
2. `copy()` - Kotlin funkce pro immutable update
3. `updateWorkout` - Room aktualizuje v databázi
4. LiveData automaticky notifikuje observery

**Proč copy() a ne mutace?**
- Kotlin best practice - immutability
- Prevence nechtěných side-effectů
- Thread-safe

### DELETE (Smazání)

**Příklad: Smazání aktivity s potvrzením**

```kotlin
// DashboardFragment.kt:266-275
private fun handleDelete(activity: Activity) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle("Smazat aktivitu?")
        .setMessage("Opravdu chcete smazat tuto aktivitu?")
        .setPositiveButton("Smazat") { _, _ ->
            deleteActivity(activity)
        }
        .setNegativeButton("Zrušit", null)
        .show()
}

// DashboardFragment.kt:277-297
private fun deleteActivity(activity: Activity) {
    lifecycleScope.launch {
        try {
            when (activity) {
                is Activity.WorkoutActivity -> {
                    repository.deleteWorkout(activity.workout)
                }
                is Activity.MealActivity -> {
                    repository.deleteMeal(activity.meal)
                }
            }
            Toast.makeText(requireContext(), "Aktivita smazána", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Chyba při mazání: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
```

**Co se děje?**
1. `MaterialAlertDialogBuilder` - potvrzovací dialog
2. `when (activity)` - sealed class type-safe dispatch
3. `deleteWorkout/deleteMeal` - smazání z databáze
4. `try-catch` - error handling

**Proč potvrzovací dialog?**
- UX best practice - prevence náhodného smazání
- Material Design guideline

---

## Kritické části kódu

### 1. Výpočet BMR (User.kt:15-24)

```kotlin
fun calculateBMR(): Double {
    return if (gender == "male") {
        // Harris-Benedict formula for men (1984 revision)
        88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * ageYears)
    } else {
        // Harris-Benedict formula for women (1984 revision)
        447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * ageYears)
    }
}

fun withCalculatedBMR(): User {
    return this.copy(bmr = calculateBMR())
}
```

**Proč Harris-Benedict (1984)?**
- Vědecky ověřený vzorec
- Zohledňuje pohlaví, váhu, výšku, věk
- Používán v medicíně

**Proč withCalculatedBMR()?**
- Immutable update pattern
- BMR se počítá až při ukládání, ne při každém vytvoření objektu

**Bez této funkce by:**
- Musel se BMR počítat manuálně všude
- Riziko chyb při opakování výpočtu
- Horší udržovatelnost

### 2. Denní tracking kalorií (DashboardFragment.kt:201-234)

```kotlin
private fun updateCalorieInfo(meals: List<Meal>, bmr: Double) {
    // Filtrovat pouze dnešní jídla
    val todayMeals = getTodayMeals(meals)
    val totalCalories = todayMeals.sumOf { it.calories }

    caloriesConsumedText.text = "$totalCalories kcal"

    // Zbývající kalorie = BMR - přijato
    val remaining = bmr - totalCalories
    caloriesRemainingText.text = "${String.format("%.0f", remaining)} kcal"

    // Progress bar (0-100%)
    val progress = if (bmr > 0) {
        ((totalCalories / bmr) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }
    calorieProgressBar.progress = progress

    // Barevná indikace podle stavu
    when {
        remaining < 0 -> {
            // Překročen limit - červená
            calorieStatusText.text = "Překročili jste denní limit o ${String.format("%.0f", -remaining)} kcal"
            caloriesRemainingText.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }
        remaining < bmr * 0.2 -> {
            // Blízko limitu - oranžová
            calorieStatusText.text = "Blížíte se k dennímu limitu"
            caloriesRemainingText.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
        }
        else -> {
            // V pořádku - zelená
            calorieStatusText.text = "Máte ještě ${String.format("%.0f", remaining)} kcal k dispozici"
            caloriesRemainingText.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        }
    }
}
```

**Klíčové výpočty:**
- **Zbývající kalorie**: `BMR - totalCalories`
- **Progress %**: `(totalCalories / BMR) * 100`
- **Práh varování**: 20% BMR zbývá

**Proč barevné indikace?**
- Vizuální feedback pro uživatele
- UX best practice - rychlé pochopení stavu
- Material Design guidelines

**Bez této funkce by:**
- Uživatel nevěděl, kolik kalorií může ještě přijmout
- Chyběl by motivační prvek
- Aplikace by nebyla užitečná pro weight management

### 3. Filtrování dnešních aktivit (DashboardFragment.kt:190-199)

```kotlin
private fun getTodayActivities(activities: List<Activity>): List<Activity> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)    // Důležité!
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfDay = calendar.timeInMillis

    return activities.filter { it.timestamp >= startOfDay }
}
```

**Proč nastavovat na půlnoc?**
- Chceme aktivity od 00:00:00.000 dnešního dne
- Bez toho by `Calendar.getInstance()` vrátil aktuální čas (např. 14:35)
- Filtrovali bychom jen aktivity od 14:35 dnes

**Příklad:**
- Nyní je 14:35
- Bez nastavení půlnoci: `startOfDay = 14:35` → filtruje aktivity od 14:35
- S nastavením půlnoci: `startOfDay = 00:00` → filtruje aktivity od půlnoci

**Bez této funkce by:**
- Dashboard ukazoval špatné "dnešní" aktivity
- Statistiky by byly nesprávné
- Motivační karta by nefungovala správně

### 4. Motivační karta pro denní cvičení (DashboardFragment.kt:157-188)

```kotlin
private fun updateDailyActivityStatus(todayActivities: List<Activity>) {
    // Zjistit, zda dnes uživatel cvičil
    val todayWorkouts = todayActivities.filterIsInstance<Activity.WorkoutActivity>()
    val hasWorkedOutToday = todayWorkouts.isNotEmpty()

    if (hasWorkedOutToday) {
        // Uživatel dnes cvičil - zobrazit úspěch
        activityStreakIcon.text = "💪"
        activityStreakText.text = "Skvělá práce! Dnes jste cvičili"

        val totalTime = todayWorkouts.sumOf { it.workout.durationMinutes }
        activityStreakMotivation.text = "Celkem ${totalTime} minut pohybové aktivity"

        activityStatusBadge.visibility = View.VISIBLE
        activityStatusBadge.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
    } else {
        // Uživatel dnes ještě necvičil - motivovat
        activityStreakIcon.text = "🔥"
        activityStreakText.text = "Dnes jste ještě necvičili"

        val motivationalMessages = listOf(
            "Zahajte svůj den pohybem!",
            "Už jen 10 minut udělá rozdíl",
            "Vaše tělo vám poděkuje",
            "Budete se cítit skvěle!",
            "Každý den je nová příležitost"
        )
        activityStreakMotivation.text = motivationalMessages.random()

        activityStatusBadge.visibility = View.GONE
    }
}
```

**Gamification prvky:**
- **Emoji**: 🔥 (motivace) vs 💪 (úspěch)
- **Dynamický text**: Různé motivační zprávy
- **Visual feedback**: Zelený checkmark při úspěchu
- **Konkrétní info**: Celkový čas cvičení dnes

**Proč random motivační zprávy?**
- Předchází monotónnosti
- Udržuje uživatele engaged
- Gamification best practice

**Bez této funkce by:**
- Chyběla motivace k pravidelnému cvičení
- Aplikace by byla jen "cold" tracker
- Nižší user engagement

### 5. Kombinování aktivit (FitTrackRepository.kt:16-38)

```kotlin
val allActivities: LiveData<List<Activity>> = MediatorLiveData<List<Activity>>().apply {
    var workouts: List<Workout> = emptyList()
    var meals: List<Meal> = emptyList()

    fun update() {
        val activities = mutableListOf<Activity>()
        activities.addAll(workouts.map { Activity.WorkoutActivity(it.id, it) })
        activities.addAll(meals.map { Activity.MealActivity(it.id, it) })
        value = activities.sortedByDescending { it.timestamp }
    }

    addSource(allWorkouts) {
        workouts = it
        update()
    }

    addSource(allMeals) {
        meals = it
        update()
    }
}
```

**Proč MediatorLiveData?**
- Kombinuje 2 LiveData zdroje (workouts + meals)
- Při změně jednoho → automaticky aktualizuje výsledek
- UI vidí jeden unified stream

**Flow diagramu:**
```
workouts změna → addSource callback → update() → sortedByDescending → UI update
meals změna    → addSource callback → update() → sortedByDescending → UI update
```

**Bez této funkce by:**
- Museli bychom observovat workouts a meals odděleně
- Složitá synchronizace v UI
- Nemohli bychom zobrazit unified timeline

---

## Lifecycle & Threading

### Lifecycle Management

#### Proč viewLifecycleOwner? (DashboardFragment.kt:103)

```kotlin
// SPRÁVNĚ - ve fragmentu
repository.user.observe(viewLifecycleOwner) { user ->
    // aktualizovat UI
}

// ŠPATNĚ - ve fragmentu
repository.user.observe(this) { user ->
    // může způsobit memory leak!
}
```

**Rozdíl mezi `this` a `viewLifecycleOwner`:**
- `this` - lifecycle fragmentu (FRAGMENT_CREATED → FRAGMENT_DESTROYED)
- `viewLifecycleOwner` - lifecycle view fragmentu (VIEW_CREATED → VIEW_DESTROYED)

**Problém s `this`:**
1. Fragment může existovat i když jeho view je zničeno (např. v back stacku)
2. Observer zůstane aktivní i bez view
3. Callback se pokusí aktualizovat neexistující view → **crash**

**Příklad scénáře:**
1. Otevřeš DashboardFragment
2. Otevřeš jiný fragment → DashboardFragment je v back stacku
3. View je zničeno, ale fragment existuje
4. Data se změní → observer callback → pokus o aktualizaci neexistujícího view → **crash**

### Threading

#### lifecycleScope vs GlobalScope

```kotlin
// SPRÁVNĚ - lifecycle-aware
lifecycleScope.launch {
    database.insertWorkout(workout)
}

// ŠPATNĚ - není lifecycle-aware
GlobalScope.launch {
    database.insertWorkout(workout)
}
```

**Problém s GlobalScope:**
- Coroutine běží i když je aktivita/fragment zničen
- Memory leaks
- Možné crashes při pokusu o přístup k UI

**lifecycleScope výhody:**
- Automatické zrušení při destroy
- Bezpečné pro UI operace
- Prevence memory leaks

#### suspend vs non-suspend funkce

```kotlin
// DAO - suspend funkce
@Insert
suspend fun insertWorkout(workout: Workout)

// Volání - musí být v coroutine
lifecycleScope.launch {
    database.insertWorkout(workout)  // OK
}

// Toto NELZE - mimo coroutine
fun saveWorkout() {
    database.insertWorkout(workout)  // COMPILE ERROR
}
```

**Proč suspend?**
- Room nedovoluje databázové operace na main threadu
- `suspend` = tato funkce musí běžet v coroutine (background thread)
- Prevence ANR (Application Not Responding)

---

## UI komponenty

### Material Design 3

Aplikace používá Material Design 3 komponenty:

**Používané komponenty:**
- `MaterialCardView` - karty s elevation a rounded corners
- `MaterialButton` - buttons s Material styling
- `MaterialAlertDialogBuilder` - potvrzovací dialogy
- `FloatingActionButton` - FAB pro přidání aktivity
- `LinearProgressIndicator` - progress bar pro kalorie

**Příklad: MaterialCardView (fragment_dashboard.xml:21-70)**
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardElevation="4dp"
    app:cardCornerRadius="12dp"
    android:layout_marginBottom="16dp">
    <!-- obsah karty -->
</com.google.android.material.card.MaterialCardView>
```

**Proč Material Design?**
- Konzistentní UX napříč Android ekosystémem
- Built-in animace a states (ripple, elevation)
- Accessibility support
- Best practices z Google

### RecyclerView

**ActivityAdapter.kt** - adapter pro zobrazení aktivit

```kotlin
class ActivityAdapter(
    private val activities: List<Activity>,
    private val onEditClick: (Activity) -> Unit,
    private val onDeleteClick: (Activity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>()
```

**Proč RecyclerView a ne ListView?**
| RecyclerView | ListView |
|--------------|----------|
| ViewHolder pattern (efektivní) | Neefektivní scrolling |
| Flexibilní layout managers | Jen vertical list |
| Animace built-in | Manuální animace |
| Lepší výkon | Horší výkon |

**ViewHolder pattern:**
- Cachuje views pro rychlý scrolling
- Prevence findViewById() při každém scroll

---

## Možné otázky na obhajobu

### Databáze & Architektura

**Q: Proč jste použili Room místo SharedPreferences?**

A: SharedPreferences je vhodný jen pro jednoduché key-value data (nastavení, flags). Pro strukturovaná data jako workouts a meals potřebujeme:
- Relační strukturu (tabulky, vztahy)
- Komplexní dotazy (filtrování, sorting, agregace)
- CRUD operace
- Type safety
- LiveData integrace

**Q: Co je Repository pattern a proč jste ho použili?**

A: Repository pattern centralizuje přístup k datům. Výhody:
- Oddělení business logiky od databázových operací
- Snadnější testování (můžeme mock repository)
- Připravenost pro více datových zdrojů (database + API)
- Kombinace více DAO do jednoho rozhraní

**Q: Co by se stalo bez FitTrackRepository?**

A: Museli bychom:
- V každém fragmentu vytvářet instance všech DAO
- Duplicitní kód pro kombinování workouts + meals
- Složitější testování
- Horší udržovatelnost

**Q: Proč máte User s id = 1? Co když bude více uživatelů?**

A: Aplikace je určena pro single-user použití (fitness tracker pro jednoho uživatele). Pro multi-user by potřebovali:
- Authentication (login/register)
- Foreign keys ve Workout a Meal (userId)
- Složitější UX
To je mimo scope semestrálního projektu.

### LiveData & Coroutines

**Q: Co je LiveData a proč jste ho použili?**

A: LiveData je lifecycle-aware observable. Výhody:
- Automatická aktualizace UI při změně dat
- Respektuje lifecycle (žádné crashes při rotaci)
- Žádné memory leaks
- Integration s Room

**Q: Co by se stalo bez LiveData?**

A: Museli bychom:
- Manuálně observovat změny v databázi
- Manuálně aktualizovat UI
- Řešit lifecycle manuálně (unsubscribe v onDestroy)
- Řešit rotation (data by zmizela)

**Q: Proč používáte lifecycleScope?**

A: lifecycleScope je lifecycle-aware coroutine scope. Výhody:
- Automatické zrušení coroutines při destroy aktivity/fragmentu
- Prevence memory leaks
- Bezpečné pro UI operace
- Main-safe (Room automaticky přepíná na background thread)

**Q: Co je suspend funkce?**

A: `suspend` keyword označuje funkci, která může být pozastavena (suspended) a následně obnovena. Používá se pro asynchronní operace (network, database). Room vyžaduje suspend pro databázové operace, aby neběžely na main threadu.

### Sealed Class

**Q: Co je Sealed Class a proč jste použili Activity jako sealed class?**

A: Sealed class je "restricted hierarchy" - všechny podtypy musí být definovány ve stejném souboru. Používám ho pro type-safe reprezentaci různých typů aktivit (Workout vs Meal). Výhody:
- Type-safe when statements (compiler ví o všech typech)
- Každý typ může mít různá data
- Lepší než Enum (může mít komplexní data)

**Q: Proč ne Enum?**

A: Enum nemůže mít různá data pro každý typ. Potřebuju:
- WorkoutActivity má Workout objekt
- MealActivity má Meal objekt
Sealed class tohle umožňuje, Enum ne.

### BMR kalkulátor

**Q: Jaký vzorec používáte pro BMR a proč?**

A: Harris-Benedict formula (revise 1984):
- **Muži**: 88.362 + (13.397 × kg) + (4.799 × cm) - (5.677 × věk)
- **Ženy**: 447.593 + (9.247 × kg) + (3.098 × cm) - (4.330 × věk)

Je to vědecky ověřený vzorec používaný v medicíně. Zohledňuje pohlaví, váhu, výšku a věk.

**Q: Co je to BMR?**

A: Basal Metabolic Rate - energie (kalorie), kterou tělo potřebuje v klidu pro základní funkce (dýchání, krevní oběh, tvorba buněk). Je to baseline pro výpočet denního kalorického příjmu.

### CRUD operace

**Q: Ukaž CRUD operace ve tvé aplikaci.**

A:
- **CREATE**: WorkoutDetailActivity.kt:108-119 - vytvoření nového workoutu
- **READ**: StatsFragment.kt:68-72 - načtení všech workoutů přes LiveData
- **UPDATE**: WorkoutDetailActivity.kt:121-135 - editace existujícího workoutu
- **DELETE**: DashboardFragment.kt:277-297 - smazání aktivity s error handling

**Q: Proč máte potvrzovací dialog před smazáním?**

A: UX best practice - prevence náhodného smazání dat. Material Design guidelines doporučují potvrzení pro destruktivní akce.

### Lifecycle

**Q: Co je to viewLifecycleOwner a proč ho používáte?**

A: Ve fragmentu má `this` (fragment lifecycle) a `viewLifecycleOwner` (view lifecycle) různé lifecycles. View může být zničeno (např. při přechodu na jiný fragment), ale fragment zůstává v back stacku. Observer na `this` by se pokusil aktualizovat neexistující view → crash. `viewLifecycleOwner` se automaticky unsubscribe při zničení view.

**Q: Co je to memory leak a jak ho předcházíte?**

A: Memory leak = objekt je v paměti, ale už není potřeba (garbage collector ho nemůže uvolnit). Předcházím pomocí:
- `viewLifecycleOwner` pro LiveData observers
- `lifecycleScope` pro coroutines
- Singleton database s `applicationContext`

### UI & UX

**Q: Proč máte motivační kartu?**

A: Gamification - motivace uživatelů k pravidelnému cvičení. Ukazuje:
- Emoji 🔥 (motivace) vs 💪 (úspěch)
- Dynamické motivační zprávy
- Konkrétní čas cvičení dnes
- Visual feedback (zelený checkmark)

**Q: Jak funguje barevná indikace kalorií?**

A:
- **Zelená**: Více než 20% BMR zbývá
- **Oranžová**: Méně než 20% BMR zbývá
- **Červená**: Překročen denní limit

Uživatel okamžitě vidí svůj stav.

---

## Demo scénář

### Příprava před demem

1. **Smaž data aplikace** (čistý start):
   ```bash
   adb shell pm clear com.example.myapp011fittrack
   ```

2. **Připrav testovací data**:
   - Pohlaví: Muž
   - Výška: 180 cm
   - Váha: 80 kg
   - Věk: 25 let
   - **BMR bude: ~1850 kcal**

### Demo flow (5-7 minut)

#### 1. První spuštění (30s)
1. Spusť aplikaci
2. Ukázat úvodní setup screen
3. Vyplnit data (muž, 180 cm, 80 kg, 25 let)
4. Kliknout "Uložit a pokračovat"

**Co říct:**
> "Při prvním spuštění se uživatel musí zaregistrovat a vyplnit své parametry. Aplikace z toho vypočítá BMR pomocí Harris-Benedict vzorce z roku 1984."

#### 2. Dashboard (1 min)
1. Ukázat BMR kartu (1850 kcal)
2. Kliknout "Zobrazit detail BMR" → ukázat výpočet
3. Zpět na dashboard
4. Ukázat motivační kartu (dnes ještě necvičili)
5. Ukázat kalorickou bilanci (0 kcal přijato, 1850 zbývá)

**Co říct:**
> "Dashboard je hlavní obrazovka. Nahoře vidíme vypočítané BMR. Motivační karta gamifikuje aplikaci - ukazuje, zda jsme dnes cvičili. Kalorická bilance ukazuje, kolik kalorií jsme přijali a kolik nám ještě zbývá do denního limitu."

#### 3. Přidání jídla (1 min)
1. Kliknout FAB (+ button)
2. Vybrat "Jídlo"
3. Vyplnit: "Snídaně", 600 kcal, "Ovesná kaše s ovocem"
4. Uložit
5. Ukázat aktualizaci: 600 přijato, 1250 zbývá, zelená barva
6. Přidat další jídlo: "Oběd", 800 kcal
7. Ukázat: 1400 přijato, 450 zbývá, **oranžová barva** (< 20% BMR)

**Co říct:**
> "Přidávám jídlo přes FAB button. Aplikace automaticky aktualizuje kalorickou bilanci. Všimněte si barevné indikace - když zbývá méně než 20% BMR, změní se na oranžovou jako varování."

#### 4. Přidání cvičení (1 min)
1. Kliknout FAB
2. Vybrat "Cvičení"
3. Vyplnit: "Běh", 30 minut, "Ranní běh v parku"
4. Uložit
5. Ukázat aktualizaci motivační karty: 💪 "Skvělá práce! Dnes jste cvičili", "Celkem 30 minut pohybové aktivity"

**Co říct:**
> "Přidávám cvičení. Motivační karta se okamžitě aktualizuje - mění se emoji z 🔥 na 💪, zobrazí se gratulace a celkový čas cvičení. To je ta gamifikace, která motivuje k pravidelnému pohybu."

#### 5. Editace a smazání (1 min)
1. Kliknout "Upravit" u běhu
2. Změnit na 45 minut
3. Uložit → motivační karta ukazuje 45 minut
4. Kliknout "Smazat" u snídaně
5. Potvrdit dialog
6. Ukázat aktualizaci: 800 přijato, 1050 zbývá, **zpět zelená barva**

**Co říct:**
> "Tady vidíme CRUD operace - UPDATE při editaci a DELETE se smazáním. Všimněte si potvrzovacího dialogu - prevence náhodného smazání. Všechna data se okamžitě aktualizují díky LiveData."

#### 6. Statistiky (1 min)
1. Přejít na "Statistiky" (bottom navigation)
2. Ukázat:
   - Celkem cvičení: 1
   - Celkem jídel: 1
   - Celkový čas: 45 min
   - Průměrná délka: 45 min
   - Cvičení dnes: 1
   - Jídla dnes: 1
   - Kalorie dnes: 800 kcal

**Co říct:**
> "Statistiky nabízejí přehled jak celkové, tak dnešní aktivity. Všechno se počítá automaticky z databáze."

#### 7. Profil a historie (1 min)
1. Přejít na "Profil" (bottom navigation)
2. Ukázat uživatelská data (Muž, 25 let, 180 cm, 80 kg, BMR 1850 kcal)
3. Ukázat historii všech aktivit (běh 45 min, oběd 800 kcal)
4. Kliknout "Upravit profil"
5. Změnit váhu na 75 kg
6. Uložit
7. Ukázat aktualizované BMR na dashboardu (~1800 kcal)

**Co říct:**
> "V profilu vidíme všechny uživatelské parametry a kompletní historii aktivit. Můžeme upravit profil - BMR se přepočítá a automaticky aktualizuje všude v aplikaci díky LiveData a Room databázi."

#### 8. Rotace a persistence (30s)
1. Být na dashboardu s daty
2. Otočit zařízení (landscape)
3. Ukázat, že všechna data zůstala
4. Zavřít aplikaci (swipe away)
5. Otevřít znovu
6. Ukázat, že všechna data jsou tu (Room database)

**Co říct:**
> "Aplikace správně řeší lifecycle - při rotaci se data neztratí. Room databáze persistuje všechna data, takže po zavření a znovu otevření aplikace jsou všechna data na místě."

### Závěr demo (30s)

**Co říct:**
> "Aplikace splňuje všechny povinné požadavky: má Room databázi se třemi entitami, kompletní CRUD operace, úvodní setup s BMR kalkulátorem a dashboard s progressem. Navíc jsem přidal denní tracking kalorií s barevnou indikací, motivační gamifikaci, uživatelský profil s editací a podrobné statistiky."

---

## Checklist před obhajobou

### Technická příprava

- [ ] **Nainstalovat APK na fyzickém zařízení** (ne emulátor - rychlejší demo)
- [ ] **Smazat data aplikace** (čistý start)
- [ ] **Připravit testovací data** (muž, 180cm, 80kg, 25 let)
- [ ] **Otestovat všechny funkce** (CRUD, rotace, persistence)
- [ ] **Nabít zařízení** (min 80%)
- [ ] **Vypnout notifikace** (neruš režim)
- [ ] **Připravit IDE** (Android Studio otevřené na klíčových souborech)

### Soubory k otevření v IDE

1. **User.kt** - BMR kalkulátor
2. **FitTrackDatabase.kt** - Room setup
3. **FitTrackRepository.kt** - Repository pattern, MediatorLiveData
4. **DashboardFragment.kt** - Denní kalorie tracking, motivační karta
5. **WorkoutDetailActivity.kt** - CRUD operace
6. **Activity.kt** - Sealed class

### Znalosti k zopakování

- [ ] **Harris-Benedict vzorec** (1984) - vzorec zpaměti
- [ ] **Room komponenty**: Entity, DAO, Database, Repository
- [ ] **LiveData vs MutableLiveData vs MediatorLiveData**
- [ ] **lifecycleScope vs GlobalScope**
- [ ] **viewLifecycleOwner vs this**
- [ ] **Sealed class vs Enum**
- [ ] **suspend funkce**
- [ ] **Repository pattern**
- [ ] **CRUD operace** - kde v kódu
- [ ] **Memory leaks** - jak předcházet

### Demo plán

- [ ] **5-7 minut** celkově
- [ ] **Setup** (30s) → **Dashboard** (1 min) → **Přidat jídlo** (1 min) → **Přidat cvičení** (1 min) → **CRUD operace** (1 min) → **Statistiky** (1 min) → **Profil** (1 min) → **Rotace** (30s)
- [ ] Připravit si co říct u každé části
- [ ] Trénovat demo alespoň 2x

### Možné otázky k promyšlení

- [ ] Proč Room a ne SharedPreferences?
- [ ] Co je Repository pattern?
- [ ] Co je LiveData?
- [ ] Proč viewLifecycleOwner?
- [ ] Co je suspend funkce?
- [ ] Jak funguje BMR kalkulátor?
- [ ] Ukázat CRUD operace v kódu
- [ ] Co je Sealed class a proč jste ho použili?
- [ ] Jak předcházíte memory leaks?
- [ ] Jak funguje filtrování "dnešních" aktivit?

### Den před obhajobou

- [ ] Přečíst celý tento dokument
- [ ] Projet všechny klíčové soubory v IDE
- [ ] Vyzkoušet demo flow 2-3x
- [ ] Dobře se vyspat

---

## Rychlá referenční karta

### Klíčové soubory

| Soubor | Řádky | Co dělá |
|--------|-------|---------|
| **User.kt** | 15-24 | Harris-Benedict BMR kalkulátor |
| **FitTrackDatabase.kt** | 10-35 | Room database singleton |
| **FitTrackRepository.kt** | 16-38 | MediatorLiveData kombinuje workouts + meals |
| **DashboardFragment.kt** | 201-234 | Denní kalorie tracking s barevnou indikací |
| **DashboardFragment.kt** | 190-199 | Filtrování dnešních aktivit (Calendar na půlnoc) |
| **DashboardFragment.kt** | 157-188 | Motivační karta s gamifikací |
| **WorkoutDetailActivity.kt** | 108-119 | CREATE operace |
| **WorkoutDetailActivity.kt** | 121-135 | UPDATE operace |
| **DashboardFragment.kt** | 277-297 | DELETE operace s error handling |
| **Activity.kt** | 5-26 | Sealed class pro type-safe aktivity |

### Vzorce

**Harris-Benedict (1984):**
- **Muž**: `88.362 + (13.397 × kg) + (4.799 × cm) - (5.677 × věk)`
- **Žena**: `447.593 + (9.247 × kg) + (3.098 × cm) - (4.330 × věk)`

**Zbývající kalorie:**
- `Zbývá = BMR - Přijato`

**Progress bar:**
- `Progress = (Přijato / BMR) * 100`

### Klíčové koncepty jednou větou

| Koncept | Definice |
|---------|----------|
| **Room** | Android abstrakce nad SQLite s compile-time ověřováním |
| **LiveData** | Lifecycle-aware observable data holder |
| **Coroutines** | Asynchronní programování bez callback hell |
| **Repository** | Centralizovaný přístup k datům, odděluje business logiku od databáze |
| **Sealed Class** | Restricted hierarchy, type-safe when statements |
| **viewLifecycleOwner** | Lifecycle view fragmentu (prevence crashes a leaks) |
| **lifecycleScope** | Lifecycle-aware coroutine scope (automatické zrušení) |
| **suspend** | Funkce může být pozastavena, musí běžet v coroutine |
| **MediatorLiveData** | Kombinuje více LiveData zdrojů |

### Zkratky pro obhajobu

- **BMR** = Basal Metabolic Rate (bazální metabolismus)
- **CRUD** = Create, Read, Update, Delete
- **DAO** = Data Access Object
- **UI** = User Interface
- **UX** = User Experience
- **ANR** = Application Not Responding

---

## Tipy na obhajobu

### Komunikace

1. **Mluv pomalu a jasně** - technické termíny vyslovuj zřetelně
2. **Používej české termíny kde možné** - "databáze" místo "database"
3. **Ukazuj v kódu** - "Tady na řádku 15 vidíme..." místo jen mluvení
4. **Vysvětluj "proč"** - "Použil jsem Room, protože..." ne jen "Použil jsem Room"
5. **Přiznej co nevíš** - "To nevím přesně, ale můžu to zjistit" je lepší než vymýšlení

### Struktura odpovědi

**Dobrá odpověď má 3 části:**

1. **Co** - "Room je Android abstrakce nad SQLite"
2. **Proč** - "Použil jsem ho, protože poskytuje compile-time ověřování dotazů"
3. **Důsledek** - "Bez Room bych musel psát SQL jako stringy a chyby by se objevily až za běhu"

**Příklad špatné odpovědi:**
> Q: Co je LiveData?
>
> A: "LiveData je observer pattern."

**Příklad dobré odpovědi:**
> Q: Co je LiveData?
>
> A: "LiveData je lifecycle-aware observable data holder. Použil jsem ho, protože automaticky aktualizuje UI při změně dat a respektuje lifecycle fragmentů. Bez LiveData bych musel manuálně observovat databázi a řešit memory leaks při rotaci obrazovky."

### Body language

- ✅ Udržuj oční kontakt
- ✅ Gestikuluj k obrazovce při ukazování kódu
- ✅ Stůj vzpřímeně
- ❌ Nedívej se jen na obrazovku
- ❌ Nečti z poznámek (používej je jen jako referenci)
- ❌ Neotoč se zády k publiku

### Při demo

1. **Komentuj co děláš** - "Teď přidám jídlo s 600 kaloriemi..."
2. **Ukazuj detaily** - "Všimněte si, jak se barva změnila na oranžovou"
3. **Vysvětluj důsledky** - "Tato změna se okamžitě projeví díky LiveData"
4. **Připrav se na chyby** - Pokud něco nefunguje, ukaž v kódu kde by to mělo být opraveno

### Při otázkách

1. **Poslechni celou otázku** - nedokončuj za examinátora
2. **Shrň otázku** - "Ptáte se, proč jsem použil Room místo SharedPreferences?"
3. **Strukturovaná odpověď** - použij 3-part strukturu výše
4. **Ukaž v kódu** - "Ukážu vám to na řádku 15 v User.kt"
5. **Přiznej limity** - "Tato implementace podporuje jen jednoho uživatele, pro více by bylo potřeba..."

---

## Možná rozšíření (pokud se zeptají)

Pokud se zeptají "Co byste ještě přidal?", máš připravené odpovědi:

### Technická rozšíření

1. **ViewModel** - MVVM architektura pro lepší separation of concerns
2. **WorkManager** - denní notifikace "Nezapomeň si zalogovat jídlo!"
3. **DataStore** - moderní náhrada SharedPreferences pro settings
4. **Charts** - MPAndroidChart pro grafy pokroku
5. **Export/Import** - backup dat do JSON/CSV
6. **Cloud sync** - Firebase pro synchronizaci mezi zařízeními

### Feature rozšíření

1. **Foto jídel** - přidat možnost vyfotit jídlo
2. **Kalorie z databáze** - API pro vyhledávání kalorií jídel
3. **GPS tracking** - mapa trasy pro běh/chůzi
4. **Týdenní přehled** - grafy pokroku za týden/měsíc
5. **Cíle** - nastavit si cílovou váhu
6. **Water tracking** - sledování příjmu tekutin
7. **Multi-user** - více profilů (rodina)

---

## Hodně štěstí! 🎓

Připravil sis solidní aplikaci s čistou architekturou. Klíčem k úspěchu je:

1. ✅ **Rozumět konceptům** - ne jen memorovat
2. ✅ **Umět vysvětlit "proč"** - ne jen "co"
3. ✅ **Ukázat v kódu** - konkrétní řádky
4. ✅ **Připravit plynulé demo** - nacvičit 2-3x
5. ✅ **Být v klidu** - máš dobrou aplikaci

**Remember:** Examinátora zajímá, jestli rozumíš tomu co jsi vytvořil, ne jestli znáš každý detail dokumentace Room nebo LiveData. Soustřeď se na "proč" jsi použil určité technologie a jaké problémy řeší.

**Pro-tip:** Když nevíš odpověď na otázku, zkus říct: "Tady jsem použil [technologii X], protože jsem viděl, že se to tak dělá v dokumentaci. Můžu to ale dál prozkoumat a vylepšit." To ukazuje, že jsi ochotný se učit.

---

*Tento dokument vytvořil Claude Code pro přípravu na obhajobu semestrálního projektu FitTrack.*

*Poslední aktualizace: 2026-01-12*
