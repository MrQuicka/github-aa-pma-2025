package com.example.myapp013aeducationgame.data

object InMemoryDatabase {

    private val users = mutableListOf<User>()
    private val questions = mutableListOf<Question>()
    private val userScores = mutableListOf<UserScore>()

    private var userIdCounter = 0
    private var userScoreIdCounter = 0

    init {
        populateInitialData()
    }

    private fun populateInitialData() {
        questions.add(Question(1, "What is the capital of France?", "Paris", "London", "Berlin", "Madrid"))
        questions.add(Question(2, "What is 2 + 2?", "4", "3", "5", "6"))
        questions.add(Question(3, "What is the color of the sky?", "Blue", "Green", "Red", "Yellow"))
        questions.add(Question(4, "What is the largest planet in our solar system?", "Jupiter", "Saturn", "Mars", "Earth"))
        questions.add(Question(5, "How many days are in a leap year?", "366", "365", "364", "367"))
        questions.add(Question(6, "What is the boiling point of water?", "100°C", "90°C", "110°C", "120°C"))
        questions.add(Question(7, "Who painted the Mona Lisa?", "Leonardo da Vinci", "Michelangelo", "Picasso", "Van Gogh"))
        questions.add(Question(8, "What is 10 × 5?", "50", "40", "60", "55"))
        questions.add(Question(9, "Which programming language is used for Android development?", "Kotlin", "Python", "JavaScript", "Ruby"))
        questions.add(Question(10, "How many continents are there?", "7", "5", "6", "8"))
        questions.add(Question(11, "What is the speed of light in vacuum?", "299,792 km/s", "150,000 km/s", "500,000 km/s", "100,000 km/s"))
        questions.add(Question(12, "What year did World War II end?", "1945", "1944", "1946", "1943"))
        questions.add(Question(13, "What is the smallest prime number?", "2", "1", "3", "0"))
        questions.add(Question(14, "Which ocean is the largest?", "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean"))
        questions.add(Question(15, "How many bits are in a byte?", "8", "4", "16", "32"))
    }

    fun getAllQuestions(): List<Question> = questions

    fun getUserByName(name: String): User? {
        return users.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun insertUser(name: String): User {
        val user = User(++userIdCounter, name)
        users.add(user)
        return user
    }

    fun insertScore(userId: Int, score: Int) {
        userScores.add(UserScore(++userScoreIdCounter, userId, score))
    }

    fun getScoresForUser(userId: Int): List<UserScore> {
        return userScores.filter { it.userId == userId }.sortedByDescending { it.score }
    }
}
