package com.example.masterapp.data

object DailyTips {

    val tips = listOf(
        "Consistency is key. Answering questionnaires regularly can provide more accurate insights from your smartwatch data.",
        "Ensure your smartwatch is fully charged. Accurate data collection requires a functional device.",
        "The more data you provide, the better the insights! Don't skip any questions in the questionnaire.",
        "Your smartwatch data is valuable. It helps provide a holistic view of your well-being.",
        "Got a new smartwatch? Make sure to sync it with this app for precise data collection.",
        "Staying hydrated can impact your heart rate. Drink enough water daily.",
        "Regular physical activity can influence your heart rate variability. Aim for at least 30 minutes a day.",
        "Sleep is crucial for recovery. Ensure you're getting quality rest every night.",
        "Remember, this app provides insights, not medical advice. Always consult with a healthcare professional about any concerns.",
        "Your feedback is essential. If a particular tip was helpful, let us know!",
        "Consistency in sleep patterns can lead to better heart rate variability over time.",
        "Stress can impact your smartwatch readings. Engage in relaxation techniques like meditation or deep breathing.",
        "Looking for patterns? Answer the questionnaires at the same time each day.",
        "Remember to wear your smartwatch as much as possible for the most accurate data collection.",
        "Keep the app updated. Regular updates ensure compatibility with the latest smartwatch models.",
        "Avoid excessive caffeine intake; it can influence heart rate readings.",
        "If you're feeling unusually tired or stressed, review your smartwatch data for patterns.",
        "Share your insights with friends or family. It can be a conversation starter about health and well-being.",
        "Taking short breaks throughout the day can positively impact your stress levels and heart rate variability.",
        "Understanding your body's rhythms is essential. Regularly check insights from the app.",
        "Data inconsistencies? Ensure your smartwatch is worn correctly and is functioning well.",
        "Your mental health is crucial. Take note of any mood shifts and discuss them with a professional if needed.",
        "Being proactive with your health can lead to long-term benefits. Regularly engage with the app's insights.",
        "Physical, mental, and emotional well-being are interconnected. Strive for balance.",
        "Smartwatches can be a tool, but they're not the only answer. Pair the insights with professional advice for a comprehensive view.",
        "Mental exercises, such as puzzles or reading, can also influence your overall well-being. Engage your mind!",
        "If you spot a sudden change in your data, reflect on any lifestyle changes or events that might have caused it.",
        "The app is a bridge between technology and health. Use it to foster positive habits.",
        "Regularly cleaning your smartwatch can ensure more accurate sensor readings.",
        "Trust your instincts. If something feels off, even if your data doesn't show it, consult with a health professional."
    )

    fun getRandomTip(): String {
        return tips.random()
    }
}
