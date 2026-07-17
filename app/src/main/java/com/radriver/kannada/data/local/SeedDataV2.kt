package com.radriver.kannada.data.local

import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Category
import com.radriver.kannada.domain.model.Level

/**
 * Vocabulary added in DB v2 (200 words): the number system beyond 15, a
 * Conversations category of everyday sentences, and four noun categories
 * (Family, Food & Eating, Time & Days, Places). Inserted by MIGRATION_1_2 for
 * upgrading installs and, via [SeedData.words], by the onCreate seed for
 * fresh installs. Same transliteration scheme as [SeedData].
 *
 * Once shipped, treat this list like [SeedData.v1Words]: append-only.
 * Vocabulary added in a future DB v3 belongs in a new list.
 */
internal object SeedDataV2 {

    val words: List<WordEntity> = buildList {
        // ---- Numbers 16-30, tens, and landmarks (all INTERMEDIATE) ----
        number("Sixteen", "ಹದಿನಾರು", "Hadinaaru")
        number("Seventeen", "ಹದಿನೇಳು", "Hadinelu")
        number("Eighteen", "ಹದಿನೆಂಟು", "Hadinentu")
        number("Nineteen", "ಹತ್ತೊಂಬತ್ತು", "Hattombattu")
        number("Twenty", "ಇಪ್ಪತ್ತು", "Ippattu")
        number("Twenty-one", "ಇಪ್ಪತ್ತೊಂದು", "Ippattondu")
        number("Twenty-two", "ಇಪ್ಪತ್ತೆರಡು", "Ippatteradu")
        number("Twenty-three", "ಇಪ್ಪತ್ತಮೂರು", "Ippattamooru")
        number("Twenty-four", "ಇಪ್ಪತ್ತನಾಲ್ಕು", "Ippattanaalku")
        number("Twenty-five", "ಇಪ್ಪತ್ತೈದು", "Ippattaidu")
        number("Twenty-six", "ಇಪ್ಪತ್ತಾರು", "Ippattaaru")
        number("Twenty-seven", "ಇಪ್ಪತ್ತೇಳು", "Ippattelu")
        number("Twenty-eight", "ಇಪ್ಪತ್ತೆಂಟು", "Ippattentu")
        number("Twenty-nine", "ಇಪ್ಪತ್ತೊಂಬತ್ತು", "Ippattombattu")
        number("Thirty", "ಮೂವತ್ತು", "Moovattu")
        number("Forty", "ನಲವತ್ತು", "Nalavattu")
        number("Fifty", "ಐವತ್ತು", "Aivattu")
        number("Sixty", "ಅರವತ್ತು", "Aravattu")
        number("Seventy", "ಎಪ್ಪತ್ತು", "Eppattu")
        number("Eighty", "ಎಂಬತ್ತು", "Embattu")
        number("Ninety", "ತೊಂಬತ್ತು", "Tombattu")
        number("Zero", "ಸೊನ್ನೆ", "Sonne")
        number("Hundred", "ನೂರು", "Nooru")
        number("Thousand", "ಸಾವಿರ", "Saavira")

        // ---- Conversations: food & ordering ----
        conversation("Water, please", "ನೀರು ಕೊಡಿ", "Neeru kodi", Level.INTERMEDIATE)
        conversation("One coffee, please", "ಒಂದು ಕಾಫಿ ಕೊಡಿ", "Ondu kaafi kodi", Level.INTERMEDIATE)
        conversation("Please give me the menu", "ಮೆನು ಕೊಡಿ", "Menu kodi", Level.INTERMEDIATE)
        conversation("The bill, please", "ಬಿಲ್ ಕೊಡಿ", "Bil kodi", Level.INTERMEDIATE)
        conversation("Is this spicy?", "ಇದು ಖಾರವಾ?", "Idu khaaravaa?", Level.INTERMEDIATE)
        conversation("Have you eaten?", "ಊಟ ಆಯಿತಾ?", "Oota aayitaa?", Level.INTERMEDIATE)
        conversation("I have eaten", "ಊಟ ಆಯಿತು", "Oota aayitu", Level.INTERMEDIATE)
        conversation("I am hungry", "ನನಗೆ ಹಸಿವಾಗಿದೆ", "Nanage hasivaagide", Level.ADVANCED)
        conversation("I am thirsty", "ನನಗೆ ಬಾಯಾರಿಕೆಯಾಗಿದೆ", "Nanage baayaarikeyaagide", Level.ADVANCED)
        conversation("Less spicy, please", "ಖಾರ ಕಡಿಮೆ ಮಾಡಿ", "Khaara kadime maadi", Level.ADVANCED)
        conversation("What would you like to eat?", "ಏನು ತಿನ್ನುತ್ತೀರಾ?", "Enu tinnutteeraa?", Level.ADVANCED)
        conversation("The food is very tasty", "ಊಟ ತುಂಬಾ ರುಚಿಯಾಗಿದೆ", "Oota tumbaa ruchiyaagide", Level.ADVANCED)
        conversation("I don't eat meat", "ನಾನು ಮಾಂಸ ತಿನ್ನುವುದಿಲ್ಲ", "Naanu maamsa tinnuvudilla", Level.ADVANCED)
        conversation("I want one more dosa", "ಇನ್ನೊಂದು ದೋಸೆ ಬೇಕು", "Innondu dose beku", Level.ADVANCED)
        conversation("What is there to drink?", "ಕುಡಿಯಲು ಏನಿದೆ?", "Kudiyalu enide?", Level.ADVANCED)

        // ---- Conversations: directions & getting around ----
        conversation("Where is the bus stand?", "ಬಸ್ ನಿಲ್ದಾಣ ಎಲ್ಲಿದೆ?", "Bas nildaana ellide?", Level.INTERMEDIATE)
        conversation("Where is the toilet?", "ಶೌಚಾಲಯ ಎಲ್ಲಿದೆ?", "Shauchaalaya ellide?", Level.INTERMEDIATE)
        conversation("Go straight", "ನೇರವಾಗಿ ಹೋಗಿ", "Neravaagi hogi", Level.INTERMEDIATE)
        conversation("Turn left", "ಎಡಕ್ಕೆ ತಿರುಗಿ", "Edakke tirugi", Level.INTERMEDIATE)
        conversation("Turn right", "ಬಲಕ್ಕೆ ತಿರುಗಿ", "Balakke tirugi", Level.INTERMEDIATE)
        conversation("Stop here", "ಇಲ್ಲಿ ನಿಲ್ಲಿಸಿ", "Illi nillisi", Level.INTERMEDIATE)
        conversation("Come here", "ಇಲ್ಲಿ ಬನ್ನಿ", "Illi banni", Level.INTERMEDIATE)
        conversation("Wait a little", "ಸ್ವಲ್ಪ ಇರಿ", "Svalpa iri", Level.INTERMEDIATE)
        conversation("How far is it?", "ಎಷ್ಟು ದೂರ ಇದೆ?", "Eshtu doora ide?", Level.ADVANCED)
        conversation("It is very close", "ತುಂಬಾ ಹತ್ತಿರ ಇದೆ", "Tumbaa hattira ide", Level.ADVANCED)
        conversation("Does this bus go to the market?", "ಈ ಬಸ್ ಮಾರುಕಟ್ಟೆಗೆ ಹೋಗುತ್ತಾ?", "Ee bas maarukattege hoguttaa?", Level.ADVANCED)
        conversation("I want to go home", "ನಾನು ಮನೆಗೆ ಹೋಗಬೇಕು", "Naanu manege hogabeku", Level.ADVANCED)
        conversation("Where are you going?", "ಎಲ್ಲಿಗೆ ಹೋಗುತ್ತಿದ್ದೀರಾ?", "Ellige hoguttiddeeraa?", Level.ADVANCED)
        conversation("I am lost", "ನಾನು ದಾರಿ ತಪ್ಪಿದ್ದೇನೆ", "Naanu daari tappiddene", Level.ADVANCED)
        conversation("Is it near here?", "ಇಲ್ಲಿ ಹತ್ತಿರ ಇದೆಯಾ?", "Illi hattira ideyaa?", Level.ADVANCED)

        // ---- Conversations: shopping ----
        conversation("How much does this cost?", "ಇದು ಎಷ್ಟು?", "Idu eshtu?", Level.INTERMEDIATE)
        conversation("What is this?", "ಇದು ಏನು?", "Idu enu?", Level.INTERMEDIATE)
        conversation("I want this", "ನನಗೆ ಇದು ಬೇಕು", "Nanage idu beku", Level.INTERMEDIATE)
        conversation("I don't want it", "ನನಗೆ ಬೇಡ", "Nanage beda", Level.INTERMEDIATE)
        conversation("Show me that", "ಅದನ್ನು ತೋರಿಸಿ", "Adannu torisi", Level.INTERMEDIATE)
        conversation("That is enough", "ಅಷ್ಟು ಸಾಕು", "Ashtu saaku", Level.INTERMEDIATE)
        conversation("Give me one kilo", "ಒಂದು ಕೆಜಿ ಕೊಡಿ", "Ondu keji kodi", Level.INTERMEDIATE)
        conversation("Give me a bag", "ಒಂದು ಚೀಲ ಕೊಡಿ", "Ondu cheela kodi", Level.INTERMEDIATE)
        conversation("It is too expensive", "ತುಂಬಾ ದುಬಾರಿ", "Tumbaa dubaari", Level.ADVANCED)
        conversation("Please reduce the price", "ಸ್ವಲ್ಪ ಕಡಿಮೆ ಮಾಡಿ", "Svalpa kadime maadi", Level.ADVANCED)
        conversation("I will take this", "ಇದನ್ನು ತೆಗೆದುಕೊಳ್ಳುತ್ತೇನೆ", "Idannu tegedukolluttene", Level.ADVANCED)
        conversation("Do you have change?", "ಚಿಲ್ಲರೆ ಇದೆಯಾ?", "Chillare ideyaa?", Level.ADVANCED)
        conversation("Is the shop open?", "ಅಂಗಡಿ ತೆರೆದಿದೆಯಾ?", "Angadi teredideyaa?", Level.ADVANCED)
        conversation("The shop is closed", "ಅಂಗಡಿ ಮುಚ್ಚಿದೆ", "Angadi muchchide", Level.ADVANCED)
        conversation("Do you want anything else?", "ಬೇರೆ ಏನಾದರೂ ಬೇಕಾ?", "Bere enaadaroo bekaa?", Level.ADVANCED)

        // ---- Conversations: small talk ----
        conversation("I don't understand", "ನನಗೆ ಅರ್ಥವಾಗಲಿಲ್ಲ", "Nanage arthavaagalilla", Level.INTERMEDIATE)
        conversation("Please say it again", "ಮತ್ತೊಮ್ಮೆ ಹೇಳಿ", "Mattomme heli", Level.INTERMEDIATE)
        conversation("I know", "ನನಗೆ ಗೊತ್ತು", "Nanage gottu", Level.INTERMEDIATE)
        conversation("I don't know", "ನನಗೆ ಗೊತ್ತಿಲ್ಲ", "Nanage gottilla", Level.INTERMEDIATE)
        conversation("No problem", "ಪರವಾಗಿಲ್ಲ", "Paravaagilla", Level.INTERMEDIATE)
        conversation("Very good", "ತುಂಬಾ ಚೆನ್ನಾಗಿದೆ", "Tumbaa chennaagide", Level.INTERMEDIATE)
        conversation("What happened?", "ಏನಾಯಿತು?", "Enaayitu?", Level.INTERMEDIATE)
        conversation("Congratulations", "ಅಭಿನಂದನೆಗಳು", "Abhinandanegalu", Level.INTERMEDIATE)
        conversation("Do you speak English?", "ನೀವು ಇಂಗ್ಲಿಷ್ ಮಾತನಾಡುತ್ತೀರಾ?", "Neevu English maatanaadutteeraa?", Level.ADVANCED)
        conversation("I know a little Kannada", "ನನಗೆ ಸ್ವಲ್ಪ ಕನ್ನಡ ಬರುತ್ತದೆ", "Nanage svalpa Kannada baruttade", Level.ADVANCED)
        conversation("I am learning Kannada", "ನಾನು ಕನ್ನಡ ಕಲಿಯುತ್ತಿದ್ದೇನೆ", "Naanu Kannada kaliyuttiddene", Level.ADVANCED)
        conversation("Please speak slowly", "ನಿಧಾನವಾಗಿ ಮಾತನಾಡಿ", "Nidhaanavaagi maatanaadi", Level.ADVANCED)
        conversation("Where are you from?", "ನೀವು ಎಲ್ಲಿಯವರು?", "Neevu elliyavaru?", Level.ADVANCED)
        conversation("What are you doing?", "ಏನು ಮಾಡುತ್ತಿದ್ದೀರಾ?", "Enu maaduttiddeeraa?", Level.ADVANCED)
        conversation("Do you understand?", "ನಿಮಗೆ ಅರ್ಥವಾಯಿತಾ?", "Nimage arthavaayitaa?", Level.ADVANCED)

        // ---- Family ----
        family("Mother", "ಅಮ್ಮ", "Amma")
        family("Father", "ಅಪ್ಪ", "Appa")
        family("Elder brother", "ಅಣ್ಣ", "Anna")
        family("Elder sister", "ಅಕ್ಕ", "Akka")
        family("Younger brother", "ತಮ್ಮ", "Tamma")
        family("Younger sister", "ತಂಗಿ", "Tangi")
        family("Son", "ಮಗ", "Maga")
        family("Daughter", "ಮಗಳು", "Magalu")
        family("Grandfather", "ಅಜ್ಜ", "Ajja")
        family("Grandmother", "ಅಜ್ಜಿ", "Ajji")
        family("Husband", "ಗಂಡ", "Ganda")
        family("Wife", "ಹೆಂಡತಿ", "Hendati")
        family("Child", "ಮಗು", "Magu")
        family("Friend", "ಸ್ನೇಹಿತ", "Snehita")
        family("Family", "ಕುಟುಂಬ", "Kutumba")
        family("Uncle (mother's brother)", "ಮಾವ", "Maava", Level.INTERMEDIATE)
        family("Aunt (father's sister)", "ಅತ್ತೆ", "Atte", Level.INTERMEDIATE)
        family("Uncle (father's younger brother)", "ಚಿಕ್ಕಪ್ಪ", "Chikkappa", Level.INTERMEDIATE)
        family("Uncle (father's elder brother)", "ದೊಡ್ಡಪ್ಪ", "Doddappa", Level.INTERMEDIATE)
        family("Aunt (mother's younger sister)", "ಚಿಕ್ಕಮ್ಮ", "Chikkamma", Level.INTERMEDIATE)
        family("Aunt (mother's elder sister)", "ದೊಡ್ಡಮ್ಮ", "Doddamma", Level.INTERMEDIATE)
        family("Son-in-law", "ಅಳಿಯ", "Aliya", Level.INTERMEDIATE)
        family("Daughter-in-law", "ಸೊಸೆ", "Sose", Level.INTERMEDIATE)
        family("Grandson", "ಮೊಮ್ಮಗ", "Mommaga", Level.INTERMEDIATE)
        family("Granddaughter", "ಮೊಮ್ಮಗಳು", "Mommagalu", Level.INTERMEDIATE)

        // ---- Food & eating ----
        food("Water", "ನೀರು", "Neeru")
        food("Rice (cooked)", "ಅನ್ನ", "Anna")
        food("Milk", "ಹಾಲು", "Haalu")
        food("Fruit", "ಹಣ್ಣು", "Hannu")
        food("Vegetable", "ತರಕಾರಿ", "Tarakaari")
        food("Banana", "ಬಾಳೆಹಣ್ಣು", "Baalehannu")
        food("Mango", "ಮಾವಿನ ಹಣ್ಣು", "Maavina hannu")
        food("Coconut", "ತೆಂಗಿನಕಾಯಿ", "Tenginakaayi")
        food("Egg", "ಮೊಟ್ಟೆ", "Motte")
        food("Flatbread (roti)", "ರೊಟ್ಟಿ", "Rotti")
        food("Salt", "ಉಪ್ಪು", "Uppu")
        food("Sugar", "ಸಕ್ಕರೆ", "Sakkare")
        food("Tea", "ಚಹಾ", "Chahaa")
        food("Coffee", "ಕಾಫಿ", "Kaafi")
        food("Meal / food", "ಊಟ", "Oota")
        food("Onion", "ಈರುಳ್ಳಿ", "Eerulli")
        food("Potato", "ಆಲೂಗಡ್ಡೆ", "Aaloogadde")
        food("Tomato", "ಟೊಮೇಟೊ", "Tomaato")
        food("Lemon", "ನಿಂಬೆಹಣ್ಣು", "Nimbehannu")
        food("Ghee", "ತುಪ್ಪ", "Tuppa")
        food("Breakfast / snack", "ತಿಂಡಿ", "Tindi", Level.INTERMEDIATE)
        food("Rasam (thin curry)", "ಸಾರು", "Saaru", Level.INTERMEDIATE)
        food("Yogurt / curd", "ಮೊಸರು", "Mosaru", Level.INTERMEDIATE)
        food("Buttermilk", "ಮಜ್ಜಿಗೆ", "Majjige", Level.INTERMEDIATE)
        food("Sweet (taste)", "ಸಿಹಿ", "Sihi", Level.INTERMEDIATE)
        food("Spicy (taste)", "ಖಾರ", "Khaara", Level.INTERMEDIATE)
        food("Bitter (taste)", "ಕಹಿ", "Kahi", Level.INTERMEDIATE)
        food("Sour (taste)", "ಹುಳಿ", "Huli", Level.INTERMEDIATE)
        food("Taste", "ರುಚಿ", "Ruchi", Level.INTERMEDIATE)
        food("Vegetable stir-fry (palya)", "ಪಲ್ಯ", "Palya", Level.INTERMEDIATE)
        food("Dosa", "ದೋಸೆ", "Dose", Level.INTERMEDIATE)
        food("Idli", "ಇಡ್ಲಿ", "Idli", Level.INTERMEDIATE)
        food("Chutney", "ಚಟ್ನಿ", "Chatni", Level.INTERMEDIATE)
        food("Jaggery", "ಬೆಲ್ಲ", "Bella", Level.INTERMEDIATE)
        food("Leafy greens", "ಸೊಪ್ಪು", "Soppu", Level.INTERMEDIATE)

        // ---- Time & days ----
        timeDay("Today", "ಇಂದು", "Indu")
        timeDay("Tomorrow", "ನಾಳೆ", "Naale")
        timeDay("Yesterday", "ನಿನ್ನೆ", "Ninne")
        timeDay("Now", "ಈಗ", "Eega")
        timeDay("Later", "ಆಮೇಲೆ", "Aamele")
        timeDay("Day", "ದಿನ", "Dina")
        timeDay("Night", "ರಾತ್ರಿ", "Raatri")
        timeDay("Morning", "ಬೆಳಿಗ್ಗೆ", "Beligge")
        timeDay("Afternoon", "ಮಧ್ಯಾಹ್ನ", "Madhyaahna")
        timeDay("Evening", "ಸಂಜೆ", "Sanje")
        timeDay("Week", "ವಾರ", "Vaara")
        timeDay("Month", "ತಿಂಗಳು", "Tingalu")
        timeDay("Year", "ವರ್ಷ", "Varsha")
        timeDay("Time", "ಸಮಯ", "Samaya")
        timeDay("Monday", "ಸೋಮವಾರ", "Somavaara", Level.INTERMEDIATE)
        timeDay("Tuesday", "ಮಂಗಳವಾರ", "Mangalavaara", Level.INTERMEDIATE)
        timeDay("Wednesday", "ಬುಧವಾರ", "Budhavaara", Level.INTERMEDIATE)
        timeDay("Thursday", "ಗುರುವಾರ", "Guruvaara", Level.INTERMEDIATE)
        timeDay("Friday", "ಶುಕ್ರವಾರ", "Shukravaara", Level.INTERMEDIATE)
        timeDay("Saturday", "ಶನಿವಾರ", "Shanivaara", Level.INTERMEDIATE)
        timeDay("Sunday", "ಭಾನುವಾರ", "Bhaanuvaara", Level.INTERMEDIATE)
        timeDay("Day after tomorrow", "ನಾಡಿದ್ದು", "Naadiddu", Level.INTERMEDIATE)
        timeDay("Minute", "ನಿಮಿಷ", "Nimisha", Level.INTERMEDIATE)
        timeDay("Hour", "ಗಂಟೆ", "Gante", Level.INTERMEDIATE)
        timeDay("Early", "ಬೇಗ", "Bega", Level.INTERMEDIATE)
        timeDay("Late", "ತಡ", "Tada", Level.INTERMEDIATE)
        timeDay("Festival", "ಹಬ್ಬ", "Habba", Level.INTERMEDIATE)
        timeDay("Holiday / leave", "ರಜೆ", "Raje", Level.INTERMEDIATE)

        // ---- Places ----
        place("House / home", "ಮನೆ", "Mane")
        place("School", "ಶಾಲೆ", "Shaale")
        place("Market", "ಮಾರುಕಟ್ಟೆ", "Maarukatte")
        place("Shop", "ಅಂಗಡಿ", "Angadi")
        place("Road", "ರಸ್ತೆ", "Raste")
        place("Town / hometown", "ಊರು", "Ooru")
        place("Village", "ಹಳ್ಳಿ", "Halli")
        place("City", "ನಗರ", "Nagara")
        place("Temple", "ದೇವಸ್ಥಾನ", "Devasthaana")
        place("Hotel / restaurant", "ಹೋಟೆಲ್", "Hotelu")
        place("Office", "ಕಚೇರಿ", "Kacheri")
        place("Hospital", "ಆಸ್ಪತ್ರೆ", "Aaspatre")
        place("Bus stand", "ಬಸ್ ನಿಲ್ದಾಣ", "Bas nildaana", Level.INTERMEDIATE)
        place("Railway station", "ರೈಲು ನಿಲ್ದಾಣ", "Railu nildaana", Level.INTERMEDIATE)
        place("Airport", "ವಿಮಾನ ನಿಲ್ದಾಣ", "Vimaana nildaana", Level.INTERMEDIATE)
        place("Bank", "ಬ್ಯಾಂಕ್", "Byaank", Level.INTERMEDIATE)
        place("Post office", "ಅಂಚೆ ಕಚೇರಿ", "Anche kacheri", Level.INTERMEDIATE)
        place("Police station", "ಪೊಲೀಸ್ ಠಾಣೆ", "Polees thaane", Level.INTERMEDIATE)
        place("Library", "ಗ್ರಂಥಾಲಯ", "Granthaalaya", Level.INTERMEDIATE)
        place("Park", "ಉದ್ಯಾನವನ", "Udyaanavana", Level.INTERMEDIATE)
        place("Bridge", "ಸೇತುವೆ", "Setuve", Level.INTERMEDIATE)
        place("River", "ನದಿ", "Nadi", Level.INTERMEDIATE)
        place("Lake", "ಕೆರೆ", "Kere", Level.INTERMEDIATE)
        place("Mountain / hill", "ಬೆಟ್ಟ", "Betta", Level.INTERMEDIATE)
        place("Forest", "ಕಾಡು", "Kaadu", Level.INTERMEDIATE)
        place("Garden / grove", "ತೋಟ", "Tota", Level.INTERMEDIATE)
        place("Playground", "ಆಟದ ಮೈದಾನ", "Aatada maidaana", Level.INTERMEDIATE)
        place("College", "ಕಾಲೇಜು", "Kaaleju", Level.INTERMEDIATE)
    }

    private fun MutableList<WordEntity>.word(
        category: Category, level: Level, en: String, kn: String, tr: String
    ) = add(
        WordEntity(english = en, kannada = kn, transliteration = tr, category = category, level = level)
    )

    /** Counting beyond 15 assumes the beginner numbers, so it starts at INTERMEDIATE. */
    private fun MutableList<WordEntity>.number(en: String, kn: String, tr: String) =
        word(Category.NUMBERS, Level.INTERMEDIATE, en, kn, tr)

    /** Sentences are never BEGINNER; the author must choose INTERMEDIATE or ADVANCED. */
    private fun MutableList<WordEntity>.conversation(en: String, kn: String, tr: String, level: Level) =
        word(Category.CONVERSATIONS, level, en, kn, tr)

    private fun MutableList<WordEntity>.family(
        en: String, kn: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.FAMILY, level, en, kn, tr)

    private fun MutableList<WordEntity>.food(
        en: String, kn: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.FOOD, level, en, kn, tr)

    private fun MutableList<WordEntity>.timeDay(
        en: String, kn: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.TIME_DAYS, level, en, kn, tr)

    private fun MutableList<WordEntity>.place(
        en: String, kn: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.PLACES, level, en, kn, tr)
}
