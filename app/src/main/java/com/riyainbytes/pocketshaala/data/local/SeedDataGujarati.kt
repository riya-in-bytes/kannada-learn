package com.riyainbytes.pocketshaala.data.local

import com.riyainbytes.pocketshaala.data.local.entity.WordEntity
import com.riyainbytes.pocketshaala.domain.model.Category
import com.riyainbytes.pocketshaala.domain.model.Language
import com.riyainbytes.pocketshaala.domain.model.Level

/**
 * Gujarati vocabulary, added in DB v4 (inserted by MIGRATION_3_4 for
 * upgrading installs and, via [SeedData.words], by the onCreate seed for
 * fresh installs).
 *
 * Gujarati is romanized-only: [WordEntity.script] holds real Gujarati script
 * purely so the gu-IN TTS voice pronounces it correctly — it is never shown.
 * The transliteration is the visible text, so it must be unique (ignoring
 * case and punctuation) across this list; the checks in [SeedData] fail fast.
 * Same transliteration scheme as [SeedData], applied to Gujarati as:
 *  - long ા is always "aa"; long ી/ઈ is "ee" inside a word but "i" at the end
 *    of one and after another vowel (Paani, Bhaai, joie) — one-syllable
 *    ઘી stays "Ghee";
 *  - ફ is "f" (as spoken); anusvara/nasal vowels are "n", or "m" before a
 *    labial (Saambhalvun);
 *  - first-person sentences that Gujarati genders use the feminine forms.
 *
 * Once shipped, append-only (same rule as the Kannada lists).
 */
internal object SeedDataGujarati {

    /**
     * Kannada English → Gujarati English, for the few entries whose English
     * names Kannada or a Kannada dish. Every other entry mirrors the Kannada
     * English exactly (enforced by the mirror check in [SeedData]); number
     * words never differ, because quiz filtering and number ordering match
     * on them.
     */
    val ENGLISH_OVERRIDES: Map<String, String> = mapOf(
        "I know a little Kannada" to "I know a little Gujarati",
        "I am learning Kannada" to "I am learning Gujarati",
        "Vegetable stir-fry (palya)" to "Vegetable dish (shaak)",
        "Rasam (thin curry)" to "Kadhi (yogurt curry)"
    )

    val words: List<WordEntity> = buildList {
        // ==== Mirrors SeedData.v1Words ====

        // ---- Greetings & essential phrases ----
        greeting("Hello", "નમસ્તે", "Namaste")
        greeting("Welcome", "પધારો", "Padhaaro")
        greeting("How are you?", "કેમ છો?", "Kem chho?")
        greeting("I am fine", "હું મજામાં છું", "Hun majaamaan chhun")
        greeting("Thank you", "આભાર", "Aabhaar")
        greeting("Please", "મહેરબાની કરીને", "Maherbaani kareene")
        greeting("Sorry / Excuse me", "માફ કરજો", "Maaf karjo")
        greeting("Yes", "હા", "Haa")
        greeting("No", "ના", "Naa")
        greeting("Okay", "સારું", "Saarun")
        greeting("Goodbye", "આવજો", "Aavjo")
        greeting("See you again", "ફરી મળીશું", "Fari maleeshun")
        greeting("What is your name?", "તમારું નામ શું છે?", "Tamaarun naam shun chhe?")
        greeting("My name is…", "મારું નામ… છે", "Maarun naam… chhe")
        greeting("Good morning", "સુપ્રભાત", "Suprabhaat")
        greeting("Good night", "શુભ રાત્રિ", "Shubh raatri")

        // ---- Numbers 1-15 (word forms — what you say aloud) ----
        number("One", "એક", "Ek", Level.BEGINNER)
        number("Two", "બે", "Be", Level.BEGINNER)
        number("Three", "ત્રણ", "Tran", Level.BEGINNER)
        number("Four", "ચાર", "Chaar", Level.BEGINNER)
        number("Five", "પાંચ", "Paanch", Level.BEGINNER)
        number("Six", "છ", "Chha", Level.BEGINNER)
        number("Seven", "સાત", "Saat", Level.BEGINNER)
        number("Eight", "આઠ", "Aath", Level.BEGINNER)
        number("Nine", "નવ", "Nav", Level.BEGINNER)
        number("Ten", "દસ", "Das", Level.BEGINNER)
        number("Eleven", "અગિયાર", "Agiyaar", Level.BEGINNER)
        number("Twelve", "બાર", "Baar", Level.BEGINNER)
        number("Thirteen", "તેર", "Ter", Level.BEGINNER)
        number("Fourteen", "ચૌદ", "Chaud", Level.BEGINNER)
        number("Fifteen", "પંદર", "Pandar", Level.BEGINNER)

        // ---- Essential verbs (dictionary forms: the -vun infinitive) ----
        verb("To eat", "ખાવું", "Khaavun")
        verb("To drink", "પીવું", "Peevun")
        verb("To go", "જવું", "Javun")
        verb("To come", "આવવું", "Aavvun")
        verb("To see", "જોવું", "Jovun")
        verb("To speak", "બોલવું", "Bolvun")
        verb("To listen / ask", "સાંભળવું", "Saambhalvun")
        verb("To give", "આપવું", "Aapvun")
        verb("To take", "લેવું", "Levun")
        verb("To do / make", "કરવું", "Karvun")
        verb("To sleep", "સૂવું", "Soovun")
        verb("To sit", "બેસવું", "Besvun")
        verb("To walk", "ચાલવું", "Chaalvun")
        verb("To read", "વાંચવું", "Vaanchvun")
        verb("To write", "લખવું", "Lakhvun")

        // ==== Mirrors SeedDataV2 ====

        // ---- Numbers 16-30, tens, and landmarks (all INTERMEDIATE) ----
        number("Sixteen", "સોળ", "Sol", Level.INTERMEDIATE)
        number("Seventeen", "સત્તર", "Sattar", Level.INTERMEDIATE)
        number("Eighteen", "અઢાર", "Adhaar", Level.INTERMEDIATE)
        number("Nineteen", "ઓગણીસ", "Oganees", Level.INTERMEDIATE)
        number("Twenty", "વીસ", "Vees", Level.INTERMEDIATE)
        number("Twenty-one", "એકવીસ", "Ekvees", Level.INTERMEDIATE)
        number("Twenty-two", "બાવીસ", "Baavees", Level.INTERMEDIATE)
        number("Twenty-three", "તેવીસ", "Tevees", Level.INTERMEDIATE)
        number("Twenty-four", "ચોવીસ", "Chovees", Level.INTERMEDIATE)
        number("Twenty-five", "પચીસ", "Pachees", Level.INTERMEDIATE)
        number("Twenty-six", "છવ્વીસ", "Chhavvees", Level.INTERMEDIATE)
        number("Twenty-seven", "સત્તાવીસ", "Sattaavees", Level.INTERMEDIATE)
        number("Twenty-eight", "અઠ્ઠાવીસ", "Atthaavees", Level.INTERMEDIATE)
        number("Twenty-nine", "ઓગણત્રીસ", "Ogantrees", Level.INTERMEDIATE)
        number("Thirty", "ત્રીસ", "Trees", Level.INTERMEDIATE)
        number("Forty", "ચાલીસ", "Chaalees", Level.INTERMEDIATE)
        number("Fifty", "પચાસ", "Pachaas", Level.INTERMEDIATE)
        number("Sixty", "સાઠ", "Saath", Level.INTERMEDIATE)
        number("Seventy", "સિત્તેર", "Sitter", Level.INTERMEDIATE)
        number("Eighty", "એંસી", "Ensi", Level.INTERMEDIATE)
        number("Ninety", "નેવું", "Nevun", Level.INTERMEDIATE)
        number("Zero", "શૂન્ય", "Shoonya", Level.INTERMEDIATE)
        number("Hundred", "સો", "So", Level.INTERMEDIATE)
        number("Thousand", "હજાર", "Hajaar", Level.INTERMEDIATE)

        // ---- Conversations: food & ordering ----
        conversation("Water, please", "પાણી આપો", "Paani aapo", Level.INTERMEDIATE)
        conversation("One coffee, please", "એક કોફી આપો", "Ek kofi aapo", Level.INTERMEDIATE)
        conversation("Please give me the menu", "મેનુ આપો", "Menu aapo", Level.INTERMEDIATE)
        conversation("The bill, please", "બિલ આપો", "Bil aapo", Level.INTERMEDIATE)
        conversation("Is this spicy?", "આ તીખું છે?", "Aa teekhun chhe?", Level.INTERMEDIATE)
        conversation("Have you eaten?", "તમે જમ્યા?", "Tame jamyaa?", Level.INTERMEDIATE)
        conversation("I have eaten", "મેં જમી લીધું", "Men jami leedhun", Level.INTERMEDIATE)
        conversation("I am hungry", "મને ભૂખ લાગી છે", "Mane bhookh laagi chhe", Level.ADVANCED)
        conversation("I am thirsty", "મને તરસ લાગી છે", "Mane taras laagi chhe", Level.ADVANCED)
        conversation("Less spicy, please", "તીખું ઓછું રાખજો", "Teekhun ochhun raakhjo", Level.ADVANCED)
        conversation("What would you like to eat?", "તમે શું ખાશો?", "Tame shun khaasho?", Level.ADVANCED)
        conversation("The food is very tasty", "જમવાનું બહુ સ્વાદિષ્ટ છે", "Jamvaanun bahu svaadisht chhe", Level.ADVANCED)
        conversation("I don't eat meat", "હું માંસ નથી ખાતી", "Hun maans nathi khaati", Level.ADVANCED)
        conversation("I want one more dosa", "મારે હજી એક ઢોસા જોઈએ છે", "Maare haji ek dhosaa joie chhe", Level.ADVANCED)
        conversation("What is there to drink?", "પીવામાં શું છે?", "Peevaamaan shun chhe?", Level.ADVANCED)

        // ---- Conversations: directions & getting around ----
        conversation("Where is the bus stand?", "બસ સ્ટેન્ડ ક્યાં છે?", "Bas stend kyaan chhe?", Level.INTERMEDIATE)
        conversation("Where is the toilet?", "ટોઇલેટ ક્યાં છે?", "Toilet kyaan chhe?", Level.INTERMEDIATE)
        conversation("Go straight", "સીધા જાઓ", "Seedhaa jaao", Level.INTERMEDIATE)
        conversation("Turn left", "ડાબી બાજુ વળો", "Daabi baaju valo", Level.INTERMEDIATE)
        conversation("Turn right", "જમણી બાજુ વળો", "Jamni baaju valo", Level.INTERMEDIATE)
        conversation("Stop here", "અહીં ઊભી રાખો", "Ahin oobhi raakho", Level.INTERMEDIATE)
        conversation("Come here", "અહીં આવો", "Ahin aavo", Level.INTERMEDIATE)
        conversation("Wait a little", "થોડી વાર રાહ જુઓ", "Thodi vaar raah juo", Level.INTERMEDIATE)
        conversation("How far is it?", "કેટલું દૂર છે?", "Ketlun door chhe?", Level.ADVANCED)
        conversation("It is very close", "બહુ નજીક છે", "Bahu najeek chhe", Level.ADVANCED)
        conversation("Does this bus go to the market?", "આ બસ બજાર જાય છે?", "Aa bas bajaar jaay chhe?", Level.ADVANCED)
        conversation("I want to go home", "મારે ઘરે જવું છે", "Maare ghare javun chhe", Level.ADVANCED)
        conversation("Where are you going?", "તમે ક્યાં જાઓ છો?", "Tame kyaan jaao chho?", Level.ADVANCED)
        conversation("I am lost", "હું રસ્તો ભૂલી ગઈ છું", "Hun rasto bhooli gai chhun", Level.ADVANCED)
        conversation("Is it near here?", "અહીં નજીકમાં છે?", "Ahin najeekmaan chhe?", Level.ADVANCED)

        // ---- Conversations: shopping ----
        conversation("How much does this cost?", "આ કેટલાનું છે?", "Aa ketlaanun chhe?", Level.INTERMEDIATE)
        conversation("What is this?", "આ શું છે?", "Aa shun chhe?", Level.INTERMEDIATE)
        conversation("I want this", "મારે આ જોઈએ છે", "Maare aa joie chhe", Level.INTERMEDIATE)
        conversation("I don't want it", "મારે નથી જોઈતું", "Maare nathi joitun", Level.INTERMEDIATE)
        conversation("Show me that", "પેલું બતાવો", "Pelun bataavo", Level.INTERMEDIATE)
        conversation("That is enough", "આટલું બસ છે", "Aatlun bas chhe", Level.INTERMEDIATE)
        conversation("Give me one kilo", "એક કિલો આપો", "Ek kilo aapo", Level.INTERMEDIATE)
        conversation("Give me a bag", "એક થેલી આપો", "Ek theli aapo", Level.INTERMEDIATE)
        conversation("It is too expensive", "બહુ મોંઘું છે", "Bahu monghun chhe", Level.ADVANCED)
        conversation("Please reduce the price", "થોડું ઓછું કરો", "Thodun ochhun karo", Level.ADVANCED)
        conversation("I will take this", "હું આ લઈશ", "Hun aa laish", Level.ADVANCED)
        conversation("Do you have change?", "છુટ્ટા પૈસા છે?", "Chhuttaa paisaa chhe?", Level.ADVANCED)
        conversation("Is the shop open?", "દુકાન ખુલ્લી છે?", "Dukaan khulli chhe?", Level.ADVANCED)
        conversation("The shop is closed", "દુકાન બંધ છે", "Dukaan bandh chhe", Level.ADVANCED)
        conversation("Do you want anything else?", "બીજું કંઈ જોઈએ છે?", "Beejun kain joie chhe?", Level.ADVANCED)

        // ---- Conversations: small talk ----
        conversation("I don't understand", "મને સમજાયું નહીં", "Mane samjaayun nahin", Level.INTERMEDIATE)
        conversation("Please say it again", "ફરી એક વાર કહો", "Fari ek vaar kaho", Level.INTERMEDIATE)
        conversation("I know", "મને ખબર છે", "Mane khabar chhe", Level.INTERMEDIATE)
        conversation("I don't know", "મને ખબર નથી", "Mane khabar nathi", Level.INTERMEDIATE)
        conversation("No problem", "કોઈ વાંધો નહીં", "Koi vaandho nahin", Level.INTERMEDIATE)
        conversation("Very good", "બહુ સરસ", "Bahu saras", Level.INTERMEDIATE)
        conversation("What happened?", "શું થયું?", "Shun thayun?", Level.INTERMEDIATE)
        conversation("Congratulations", "અભિનંદન", "Abhinandan", Level.INTERMEDIATE)
        conversation("Do you speak English?", "તમે અંગ્રેજી બોલો છો?", "Tame Angreji bolo chho?", Level.ADVANCED)
        conversation("I know a little Gujarati", "મને થોડું ગુજરાતી આવડે છે", "Mane thodun Gujaraati aavde chhe", Level.ADVANCED)
        conversation("I am learning Gujarati", "હું ગુજરાતી શીખું છું", "Hun Gujaraati sheekhun chhun", Level.ADVANCED)
        conversation("Please speak slowly", "ધીમે ધીમે બોલો", "Dheeme dheeme bolo", Level.ADVANCED)
        conversation("Where are you from?", "તમે ક્યાંના છો?", "Tame kyaannaa chho?", Level.ADVANCED)
        conversation("What are you doing?", "તમે શું કરો છો?", "Tame shun karo chho?", Level.ADVANCED)
        conversation("Do you understand?", "તમને સમજાયું?", "Tamne samjaayun?", Level.ADVANCED)

        // ---- Family ----
        family("Mother", "મા", "Maa")
        family("Father", "પપ્પા", "Pappaa")
        family("Elder brother", "મોટા ભાઈ", "Motaa bhaai")
        family("Elder sister", "મોટી બેન", "Moti ben")
        family("Younger brother", "નાનો ભાઈ", "Naano bhaai")
        family("Younger sister", "નાની બેન", "Naani ben")
        family("Son", "દીકરો", "Deekro")
        family("Daughter", "દીકરી", "Deekri")
        family("Grandfather", "દાદા", "Daadaa")
        family("Grandmother", "દાદી", "Daadi")
        family("Husband", "પતિ", "Pati")
        family("Wife", "પત્ની", "Patni")
        family("Child", "બાળક", "Baalak")
        family("Friend", "મિત્ર", "Mitra")
        family("Family", "પરિવાર", "Parivaar")
        family("Uncle (mother's brother)", "મામા", "Maamaa", Level.INTERMEDIATE)
        family("Aunt (father's sister)", "ફઈ", "Fai", Level.INTERMEDIATE)
        family("Uncle (father's younger brother)", "કાકા", "Kaakaa", Level.INTERMEDIATE)
        family("Uncle (father's elder brother)", "મોટા કાકા", "Motaa kaakaa", Level.INTERMEDIATE)
        family("Aunt (mother's younger sister)", "નાની માસી", "Naani maasi", Level.INTERMEDIATE)
        family("Aunt (mother's elder sister)", "મોટી માસી", "Moti maasi", Level.INTERMEDIATE)
        family("Son-in-law", "જમાઈ", "Jamaai", Level.INTERMEDIATE)
        family("Daughter-in-law", "વહુ", "Vahu", Level.INTERMEDIATE)
        family("Grandson", "પૌત્ર", "Pautra", Level.INTERMEDIATE)
        family("Granddaughter", "પૌત્રી", "Pautri", Level.INTERMEDIATE)

        // ---- Food & eating ----
        food("Water", "પાણી", "Paani")
        food("Rice (cooked)", "ભાત", "Bhaat")
        food("Milk", "દૂધ", "Doodh")
        food("Fruit", "ફળ", "Fal")
        food("Vegetable", "શાકભાજી", "Shaakbhaaji")
        food("Banana", "કેળું", "Kelun")
        food("Mango", "કેરી", "Keri")
        food("Coconut", "નાળિયેર", "Naaliyer")
        food("Egg", "ઈંડું", "Eendun")
        food("Flatbread (roti)", "રોટલી", "Rotli")
        food("Salt", "મીઠું", "Meethun")
        food("Sugar", "ખાંડ", "Khaand")
        food("Tea", "ચા", "Chaa")
        food("Coffee", "કોફી", "Kofi")
        food("Meal / food", "જમવાનું", "Jamvaanun")
        food("Onion", "ડુંગળી", "Dungli")
        food("Potato", "બટાકા", "Bataakaa")
        food("Tomato", "ટામેટું", "Taametun")
        food("Lemon", "લીંબુ", "Leembu")
        food("Ghee", "ઘી", "Ghee")
        food("Breakfast / snack", "નાસ્તો", "Naasto", Level.INTERMEDIATE)
        food("Kadhi (yogurt curry)", "કઢી", "Kadhi", Level.INTERMEDIATE)
        food("Yogurt / curd", "દહીં", "Dahin", Level.INTERMEDIATE)
        food("Buttermilk", "છાશ", "Chhaash", Level.INTERMEDIATE)
        food("Sweet (taste)", "ગળ્યું", "Galyun", Level.INTERMEDIATE)
        food("Spicy (taste)", "તીખું", "Teekhun", Level.INTERMEDIATE)
        food("Bitter (taste)", "કડવું", "Kadvun", Level.INTERMEDIATE)
        food("Sour (taste)", "ખાટું", "Khaatun", Level.INTERMEDIATE)
        food("Taste", "સ્વાદ", "Svaad", Level.INTERMEDIATE)
        food("Vegetable dish (shaak)", "શાક", "Shaak", Level.INTERMEDIATE)
        food("Dosa", "ઢોસા", "Dhosaa", Level.INTERMEDIATE)
        food("Idli", "ઇડલી", "Idli", Level.INTERMEDIATE)
        food("Chutney", "ચટણી", "Chatni", Level.INTERMEDIATE)
        food("Jaggery", "ગોળ", "Gol", Level.INTERMEDIATE)
        food("Leafy greens", "ભાજી", "Bhaaji", Level.INTERMEDIATE)

        // ---- Time & days ----
        timeDay("Today", "આજે", "Aaje")
        timeDay("Tomorrow", "આવતી કાલે", "Aavti kaale")
        timeDay("Yesterday", "ગઈ કાલે", "Gai kaale")
        timeDay("Now", "અત્યારે", "Atyaare")
        timeDay("Later", "પછી", "Pachhi")
        timeDay("Day", "દિવસ", "Divas")
        timeDay("Night", "રાત", "Raat")
        timeDay("Morning", "સવાર", "Savaar")
        timeDay("Afternoon", "બપોર", "Bapor")
        timeDay("Evening", "સાંજ", "Saanj")
        timeDay("Week", "અઠવાડિયું", "Athvaadiyun")
        timeDay("Month", "મહિનો", "Mahino")
        timeDay("Year", "વર્ષ", "Varsh")
        timeDay("Time", "સમય", "Samay")
        timeDay("Monday", "સોમવાર", "Somvaar", Level.INTERMEDIATE)
        timeDay("Tuesday", "મંગળવાર", "Mangalvaar", Level.INTERMEDIATE)
        timeDay("Wednesday", "બુધવાર", "Budhvaar", Level.INTERMEDIATE)
        timeDay("Thursday", "ગુરુવાર", "Guruvaar", Level.INTERMEDIATE)
        timeDay("Friday", "શુક્રવાર", "Shukravaar", Level.INTERMEDIATE)
        timeDay("Saturday", "શનિવાર", "Shanivaar", Level.INTERMEDIATE)
        timeDay("Sunday", "રવિવાર", "Ravivaar", Level.INTERMEDIATE)
        timeDay("Day after tomorrow", "પરમ દિવસે", "Param divase", Level.INTERMEDIATE)
        timeDay("Minute", "મિનિટ", "Minit", Level.INTERMEDIATE)
        timeDay("Hour", "કલાક", "Kalaak", Level.INTERMEDIATE)
        timeDay("Early", "વહેલું", "Vahelun", Level.INTERMEDIATE)
        timeDay("Late", "મોડું", "Modun", Level.INTERMEDIATE)
        timeDay("Festival", "તહેવાર", "Tahevaar", Level.INTERMEDIATE)
        timeDay("Holiday / leave", "રજા", "Rajaa", Level.INTERMEDIATE)

        // ---- Places ----
        place("House / home", "ઘર", "Ghar")
        place("School", "શાળા", "Shaalaa")
        place("Market", "બજાર", "Bajaar")
        place("Shop", "દુકાન", "Dukaan")
        place("Road", "રસ્તો", "Rasto")
        place("Town / hometown", "ગામ", "Gaam")
        place("Village", "ગામડું", "Gaamdun")
        place("City", "શહેર", "Shaher")
        place("Temple", "મંદિર", "Mandir")
        place("Hotel / restaurant", "હોટલ", "Hotal")
        place("Office", "ઓફિસ", "Ofis")
        place("Hospital", "દવાખાનું", "Davaakhaanun")
        place("Bus stand", "બસ સ્ટેન્ડ", "Bas stend", Level.INTERMEDIATE)
        place("Railway station", "રેલવે સ્ટેશન", "Relve steshan", Level.INTERMEDIATE)
        place("Airport", "એરપોર્ટ", "Erport", Level.INTERMEDIATE)
        place("Bank", "બેંક", "Benk", Level.INTERMEDIATE)
        place("Post office", "પોસ્ટ ઓફિસ", "Post ofis", Level.INTERMEDIATE)
        place("Police station", "પોલીસ સ્ટેશન", "Polees steshan", Level.INTERMEDIATE)
        place("Library", "પુસ્તકાલય", "Pustakaalay", Level.INTERMEDIATE)
        place("Park", "બગીચો", "Bageecho", Level.INTERMEDIATE)
        place("Bridge", "પુલ", "Pul", Level.INTERMEDIATE)
        place("River", "નદી", "Nadi", Level.INTERMEDIATE)
        place("Lake", "તળાવ", "Talaav", Level.INTERMEDIATE)
        place("Mountain / hill", "ડુંગર", "Dungar", Level.INTERMEDIATE)
        place("Forest", "જંગલ", "Jangal", Level.INTERMEDIATE)
        place("Garden / grove", "વાડી", "Vaadi", Level.INTERMEDIATE)
        place("Playground", "રમતનું મેદાન", "Ramatnun medaan", Level.INTERMEDIATE)
        place("College", "કોલેજ", "Kolej", Level.INTERMEDIATE)

        // ==== Mirrors SeedDataV3 ====

        // ---- Round number that completes the quiz-friendly set ----
        number("Five hundred", "પાંચસો", "Paanchso", Level.INTERMEDIATE)

        // ---- The other hundreds, so the Numbers tab counts 100, 200 … 900 ----
        number("Two hundred", "બસો", "Baso", Level.INTERMEDIATE)
        number("Three hundred", "ત્રણસો", "Transo", Level.INTERMEDIATE)
        number("Four hundred", "ચારસો", "Chaarso", Level.INTERMEDIATE)
        number("Six hundred", "છસો", "Chhaso", Level.INTERMEDIATE)
        number("Seven hundred", "સાતસો", "Saatso", Level.INTERMEDIATE)
        number("Eight hundred", "આઠસો", "Aathso", Level.INTERMEDIATE)
        number("Nine hundred", "નવસો", "Navso", Level.INTERMEDIATE)

        // ---- Conversations: more everyday sentences ----
        conversation("What is the time?", "કેટલા વાગ્યા?", "Ketlaa vaagyaa?", Level.INTERMEDIATE)
        conversation("Let's go", "ચાલો", "Chaalo", Level.INTERMEDIATE)
    }

    private fun MutableList<WordEntity>.word(
        category: Category, level: Level, en: String, gu: String, tr: String
    ) = add(
        WordEntity(
            english = en,
            script = gu,
            transliteration = tr,
            category = category,
            level = level,
            language = Language.GUJARATI
        )
    )

    private fun MutableList<WordEntity>.greeting(en: String, gu: String, tr: String) =
        word(Category.GREETINGS, Level.BEGINNER, en, gu, tr)

    private fun MutableList<WordEntity>.verb(en: String, gu: String, tr: String) =
        word(Category.VERBS, Level.BEGINNER, en, gu, tr)

    /** BEGINNER for 1-15 (Kannada v1), INTERMEDIATE beyond (Kannada v2/v3). */
    private fun MutableList<WordEntity>.number(en: String, gu: String, tr: String, level: Level) =
        word(Category.NUMBERS, level, en, gu, tr)

    /** Sentences are never BEGINNER; the level mirrors the Kannada sentence. */
    private fun MutableList<WordEntity>.conversation(en: String, gu: String, tr: String, level: Level) =
        word(Category.CONVERSATIONS, level, en, gu, tr)

    private fun MutableList<WordEntity>.family(
        en: String, gu: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.FAMILY, level, en, gu, tr)

    private fun MutableList<WordEntity>.food(
        en: String, gu: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.FOOD, level, en, gu, tr)

    private fun MutableList<WordEntity>.timeDay(
        en: String, gu: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.TIME_DAYS, level, en, gu, tr)

    private fun MutableList<WordEntity>.place(
        en: String, gu: String, tr: String, level: Level = Level.BEGINNER
    ) = word(Category.PLACES, level, en, gu, tr)
}
