package com.radriver.kannada.data.local

import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category

/**
 * The embedded starter vocabulary: 46 fundamental words and phrases
 * (16 greetings/phrases, numbers 1-15, 15 essential verbs).
 *
 * Transliteration scheme (informal, learner-friendly):
 *  - long "aa" (ಾ), "ee" (ೀ), "oo" (ೂ) vowels are doubled;
 *  - Kannada ē/ō are written as plain e/o (closest English reading);
 *  - aspirates keep h (dh, bh, th); retroflex consonants are unmarked.
 */
object SeedData {

    val words: List<WordEntity> = buildList {
        // ---- Greetings & essential phrases ----
        greeting("Hello", "ನಮಸ್ಕಾರ", "Namaskaara")
        greeting("Welcome", "ಸುಸ್ವಾಗತ", "Susvaagata")
        greeting("How are you?", "ಹೇಗಿದ್ದೀರಾ?", "Hegiddeeraa?")
        greeting("I am fine", "ನಾನು ಚೆನ್ನಾಗಿದ್ದೇನೆ", "Naanu chennaagiddene")
        greeting("Thank you", "ಧನ್ಯವಾದಗಳು", "Dhanyavaadagalu")
        greeting("Please", "ದಯವಿಟ್ಟು", "Dayavittu")
        greeting("Sorry / Excuse me", "ಕ್ಷಮಿಸಿ", "Kshamisi")
        greeting("Yes", "ಹೌದು", "Haudu")
        greeting("No", "ಇಲ್ಲ", "Illa")
        greeting("Okay", "ಸರಿ", "Sari")
        greeting("Goodbye", "ಹೋಗಿ ಬನ್ನಿ", "Hogi banni")
        greeting("See you again", "ಮತ್ತೆ ಸಿಗೋಣ", "Matte sigona")
        greeting("What is your name?", "ನಿಮ್ಮ ಹೆಸರೇನು?", "Nimma hesarenu?")
        greeting("My name is…", "ನನ್ನ ಹೆಸರು…", "Nanna hesaru…")
        greeting("Good morning", "ಶುಭೋದಯ", "Shubhodaya")
        greeting("Good night", "ಶುಭ ರಾತ್ರಿ", "Shubha raatri")

        // ---- Numbers 1-15 (word forms — what you say aloud) ----
        number("One", "ಒಂದು", "Ondu")
        number("Two", "ಎರಡು", "Eradu")
        number("Three", "ಮೂರು", "Mooru")
        number("Four", "ನಾಲ್ಕು", "Naalku")
        number("Five", "ಐದು", "Aidu")
        number("Six", "ಆರು", "Aaru")
        number("Seven", "ಏಳು", "Elu")
        number("Eight", "ಎಂಟು", "Entu")
        number("Nine", "ಒಂಬತ್ತು", "Ombattu")
        number("Ten", "ಹತ್ತು", "Hattu")
        number("Eleven", "ಹನ್ನೊಂದು", "Hannondu")
        number("Twelve", "ಹನ್ನೆರಡು", "Hanneradu")
        number("Thirteen", "ಹದಿಮೂರು", "Hadimooru")
        number("Fourteen", "ಹದಿನಾಲ್ಕು", "Hadinaalku")
        number("Fifteen", "ಹದಿನೈದು", "Hadinaidu")

        // ---- Essential verbs (dictionary/root forms) ----
        verb("To eat", "ತಿನ್ನು", "Tinnu")
        verb("To drink", "ಕುಡಿ", "Kudi")
        verb("To go", "ಹೋಗು", "Hogu")
        verb("To come", "ಬರು", "Baru")
        verb("To see", "ನೋಡು", "Nodu")
        verb("To speak", "ಮಾತನಾಡು", "Maatanaadu")
        verb("To listen / ask", "ಕೇಳು", "Kelu")
        verb("To give", "ಕೊಡು", "Kodu")
        verb("To take", "ತೆಗೆದುಕೊಳ್ಳು", "Tegedukollu")
        verb("To do / make", "ಮಾಡು", "Maadu")
        verb("To sleep", "ಮಲಗು", "Malagu")
        verb("To sit", "ಕುಳಿತುಕೊಳ್ಳು", "Kulitukollu")
        verb("To walk", "ನಡೆ", "Nade")
        verb("To read", "ಓದು", "Odu")
        verb("To write", "ಬರೆ", "Bare")
    }

    private fun MutableList<WordEntity>.greeting(en: String, kn: String, tr: String) =
        add(WordEntity(english = en, kannada = kn, transliteration = tr, category = Category.GREETINGS))

    private fun MutableList<WordEntity>.number(en: String, kn: String, tr: String) =
        add(WordEntity(english = en, kannada = kn, transliteration = tr, category = Category.NUMBERS))

    private fun MutableList<WordEntity>.verb(en: String, kn: String, tr: String) =
        add(WordEntity(english = en, kannada = kn, transliteration = tr, category = Category.VERBS))
}
