# Studijní materiály - Programování mobilních aplikací (Android)
## Komplexní průvodce s detailními vysvětleními

## Obsah
1. [Základy Android vývoje](#1-základy-android-vývoje)
2. [Layouty a UI komponenty](#2-layouty-a-ui-komponenty)
3. [Aktivity a navigace mezi nimi](#3-aktivity-a-navigace-mezi-nimi)
4. [Toast a Snackbar](#4-toast-a-snackbar)
5. [Jetpack Compose](#5-jetpack-compose)
6. [Fragmenty](#6-fragmenty)
7. [Ukládání dat - SharedPreferences](#7-ukládání-dat---sharedpreferences)
8. [Práce s galerií a obrázky](#8-práce-s-galerií-a-obrázky)
9. [Room Database](#9-room-database)
10. [DataStore](#10-datastore)
11. [Cloud databáze (Firebase)](#11-cloud-databáze-firebase)
12. [Navigation Component](#12-navigation-component)
13. [Doporučené postupy a tipy](#13-doporučené-postupy-a-tipy)

---

## 1. Základy Android vývoje

### 1.1 Co je Android aplikace?

Android aplikace je softwarový program určený pro zařízení s operačním systémem Android. Moderní Android aplikace se vyvíjejí primárně v jazyce **Kotlin**, který je od roku 2019 oficiálně doporučovaný jazyk od Google. Starší aplikace jsou psané v **Java**, ale pro nové projekty se doporučuje Kotlin.

**Proč Kotlin?**
- Stručnější syntax než Java (méně boilerplate kódu)
- Null safety - výrazně snižuje NPE (NullPointerException)
- Moderní funkce jazyka (extension functions, coroutines, data classes)
- Plná kompatibilita s Java kódem
- Lepší podpora pro asynchronní programování

### 1.2 Struktura Android projektu

Když vytvoříš nový Android projekt v Android Studiu, vygeneruje se následující struktura:

```
MyAndroidApp/                           # Kořenový adresář projektu
│
├── app/                                # Hlavní modul aplikace
│   ├── manifests/
│   │   └── AndroidManifest.xml        # KRITICKÝ soubor - deklaruje komponenty app
│   │
│   ├── java/                          # Obsahuje Kotlin/Java zdrojové kódy
│   │   └── com.example.myapp/         # Package name tvé aplikace
│   │       ├── MainActivity.kt        # Hlavní aktivita (vstupní bod)
│   │       ├── SecondActivity.kt      # Další aktivity
│   │       └── fragments/             # Složka pro fragmenty (volitelné)
│   │           └── HomeFragment.kt
│   │
│   ├── res/                           # Resources - vše co není kód
│   │   ├── drawable/                  # Obrázky, ikony, vektorová grafika
│   │   │   ├── ic_launcher.png        # PNG obrázky
│   │   │   └── ic_home.xml            # Vektorové drawable (XML)
│   │   │
│   │   ├── layout/                    # XML soubory definující UI
│   │   │   ├── activity_main.xml      # Layout pro MainActivity
│   │   │   └── fragment_home.xml      # Layout pro HomeFragment
│   │   │
│   │   ├── values/                    # Hodnoty - stringy, barvy, styly
│   │   │   ├── strings.xml            # Všechny textové řetězce
│   │   │   ├── colors.xml             # Barevná paleta aplikace
│   │   │   ├── themes.xml             # Téma aplikace
│   │   │   └── dimens.xml             # Rozměry (margins, padding)
│   │   │
│   │   └── mipmap/                    # Launcher ikony v různých velikostech
│   │       ├── ic_launcher.png        # Ikona aplikace
│   │       └── ic_launcher_round.png  # Kulatá varianta
│   │
│   └── build.gradle                   # Build konfigurace pro app modul
│
├── gradle/                            # Gradle wrapper soubory
├── build.gradle                       # Build konfigurace pro celý projekt
└── settings.gradle                    # Nastavení Gradle projektu
```

**Důležité soubory a jejich účel:**

#### AndroidManifest.xml (app/manifests/AndroidManifest.xml)
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">
    
    <!-- OPRÁVNĚNÍ - co aplikace potřebuje od systému -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />
    
    <!-- APLIKACE - hlavní kontejner -->
    <application
        android:allowBackup="true"                    <!-- Povolit zálohu dat -->
        android:icon="@mipmap/ic_launcher"           <!-- Ikona aplikace -->
        android:label="@string/app_name"             <!-- Název aplikace -->
        android:theme="@style/Theme.MyApp">          <!-- Téma aplikace -->
        
        <!-- LAUNCHER AKTIVITA - ta, která se spustí první -->
        <activity 
            android:name=".MainActivity"              <!-- Jméno třídy aktivity -->
            android:exported="true">                  <!-- Může být spuštěna zvenčí -->
            
            <!-- INTENT FILTER - definuje, jak se aktivita spouští -->
            <intent-filter>
                <!-- MAIN = hlavní vstupní bod -->
                <action android:name="android.intent.action.MAIN" />
                
                <!-- LAUNCHER = objeví se v launcheru (menu aplikací) -->
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- DALŠÍ AKTIVITY - musí být zde zaregistrované -->
        <activity 
            android:name=".SecondActivity"
            android:parentActivityName=".MainActivity" /> <!-- Definuje zpětnou navigaci -->
    </application>
</manifest>
```

**Vysvětlení klíčových konceptů:**
- `package` - unikátní identifikátor tvé aplikace (nelze mít dvě aplikace se stejným package na zařízení)
- `android:exported="true"` - aktivita může být spuštěna z jiných aplikací nebo systému
- `intent-filter` - říká systému, kdy a jak může aktivitu spustit
- Každá Activity/Service/BroadcastReceiver MUSÍ být zaregistrovaný v manifestu

### 1.3 Activity Lifecycle - Životní cyklus aktivity

Activity (obrazovka aplikace) prochází během své existence různými stavy. Pochopení životního cyklu je ZÁSADNÍ pro správnou funkci aplikace.

**Diagram životního cyklu:**
```
[Aktivita není vytvořená]
          ↓
    onCreate() ←――――――――――――┐
          ↓                  │
    onStart() ←――――――┐      │
          ↓           │      │
    onResume()        │      │
          ↓           │      │
    [RUNNING]         │      │
          ↓           │      │
    onPause() ――――――→ │      │
          ↓                  │
    onStop() ―――――――――┘      │
          ↓                  │
    onDestroy() ―――――――――――→ ┘
          ↓
[Aktivita je zničená]
```

**Detailní vysvětlení každé metody:**

```kotlin
/**
 * Umístění: app/java/com.example.myapp/MainActivity.kt
 * 
 * MainActivity je hlavní aktivita aplikace.
 * Dědí z AppCompatActivity, která poskytuje zpětnou kompatibilitu
 * s novějšími Android features na starších verzích systému.
 */
class MainActivity : AppCompatActivity() {
    
    // Proměnné třídy - drží stav aktivity
    private lateinit var binding: ActivityMainBinding  // View binding pro přístup k UI
    private var counter: Int = 0                       // Příklad dat, která musíme zachovat
    
    /**
     * onCreate() - PRVNÍ metoda, která se volá při vytvoření aktivity
     * 
     * Co se zde děje:
     * 1. Aktivita je vytvořena v paměti
     * 2. Načte se layout z XML souboru
     * 3. Inicializují se všechny view komponenty
     * 
     * @param savedInstanceState - Bundle obsahující uložený stav
     *                             Je null při prvním spuštění
     *                             Obsahuje data při recreate (např. otočení obrazovky)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  // VŽDY musí být první řádek!
        
        // Inicializace View Binding - moderní způsob přístupu k views
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Nastavení layoutu jako obsah aktivity
        
        // Obnovení uloženého stavu (důležité pro otočení obrazovky!)
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("COUNTER_KEY", 0)
            Log.d("MainActivity", "Obnovuji stav, counter = $counter")
        }
        
        // Inicializace UI komponent - nastavení listenerů
        binding.button.setOnClickListener {
            counter++
            binding.textView.text = "Počet kliknutí: $counter"
        }
        
        Log.d("MainActivity", "onCreate() voláno")
        
        // ČAS: Trvá ~100-500ms v závislosti na složitosti layoutu
        // STAV: Aktivita EXISTUJE, ale není viditelná
    }
    
    /**
     * onStart() - Aktivita se stává VIDITELNOU pro uživatele
     * 
     * Volá se:
     * - Po onCreate() při prvním spuštění
     * - Po onRestart() když se uživatel vrací zpět
     * - Když přestane být překrytá dialogem/jinou aktivitou
     */
    override fun onStart() {
        super.onStart()  // VŽDY volat super metodu!
        
        Log.d("MainActivity", "onStart() voláno - aktivita je viditelná")
        
        // Zde můžeš:
        // - Registrovat broadcast receivers
        // - Spustit animace
        // - Připravit data pro zobrazení
        
        // STAV: Aktivita je VIDITELNÁ, ale ještě nemá focus
        // ČAS: Velmi rychlé ~50ms
    }
    
    /**
     * onResume() - Aktivita je v POPŘEDÍ a má focus
     * 
     * Toto je stav, kdy uživatel AKTIVNĚ pracuje s aplikací!
     * Aktivita je připravená přijímat input.
     */
    override fun onResume() {
        super.onResume()
        
        Log.d("MainActivity", "onResume() voláno - aktivita má focus")
        
        // Zde SPUSŤ:
        // - Kameru (pokud ji používáš)
        // - Senzory (GPS, akcelerometr, atd.)
        // - Animace
        // - Audio/video přehrávání
        // - Aktualizaci dat ze serveru
        
        // Příklad: Start audio přehrávače
        // mediaPlayer?.start()
        
        // STAV: [RUNNING] - aplikace je aktivní!
        // Zde aplikace stráví většinu času
    }
    
    /**
     * onPause() - Aktivita ZTRÁCÍ focus
     * 
     * Volá se když:
     * - Uživatel přechází na jinou aktivitu
     * - Zobrazí se dialog
     * - Obrazovka se vypíná
     * 
     * DŮLEŽITÉ: Tato metoda MUSÍ být RYCHLÁ! (< 500ms)
     * Dokud neskončí, druhá aktivita se nespustí!
     */
    override fun onPause() {
        super.onPause()
        
        Log.d("MainActivity", "onPause() voláno - ztrácíme focus")
        
        // Zde ZASTAVÍ:
        // - Animace
        // - Audio/video přehrávání
        // - Kameru
        // - GPS a další senzory
        // - Časově náročné operace
        
        // Příklad: Pauza audio přehrávače
        // mediaPlayer?.pause()
        
        // ULOŽ lehká data do SharedPreferences
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("lastCounter", counter).apply()
        
        // NIKDY zde nedělej:
        // - Ukládání do databáze (může být pomalé)
        // - Network požadavky
        // - Těžké výpočty
        
        // STAV: Aktivita je stále částečně viditelná
        // ČAS: MUSÍ být rychlé! Max 500ms
    }
    
    /**
     * onStop() - Aktivita NENÍ VIDITELNÁ
     * 
     * Volá se když:
     * - Uživatel přešel na jinou aplikaci
     * - Stiskl home button
     * - Jiná aktivita překryla celou obrazovku
     */
    override fun onStop() {
        super.onStop()
        
        Log.d("MainActivity", "onStop() voláno - nejsme viditelní")
        
        // Zde můžeš:
        // - Uložit data do databáze (máš víc času než v onPause)
        // - Uvolnit těžké resources
        // - Zrušit registrace receivers
        
        // Příklad: Uložení většího množství dat
        // viewModel.saveDataToDatabase()
        
        // Unregister receivers
        // unregisterReceiver(myReceiver)
        
        // STAV: Aktivita není viditelná, ale STÁLE V PAMĚTI
        // Systém může aktivitu KDYKOLIV ukončit pro uvolnění RAM!
    }
    
    /**
     * onDestroy() - Aktivita je NIČENA
     * 
     * Volá se když:
     * - Uživatel zavře aktivitu (finish())
     * - Systém ukončí proces pro uvolnění paměti
     * - Otočení obrazovky (configuration change)
     */
    override fun onDestroy() {
        super.onDestroy()
        
        Log.d("MainActivity", "onDestroy() voláno - aktivita končí")
        
        // Zde UVOLNI vše:
        // - Database connections
        // - Network connections  
        // - References na view (prevence memory leak!)
        // - Listeners
        
        // Příklad: Cleanup
        // mediaPlayer?.release()
        // mediaPlayer = null
        
        // STAV: Aktivita bude SMAZÁNA z paměti
        // Po této metodě již aktivita NEEXISTUJE
    }
    
    /**
     * onSaveInstanceState() - Uložení dočasného stavu
     * 
     * Volá se před onStop(), když systém může aktivitu zničit.
     * Slouží k uchování UI stavu (např. text v EditText, scroll pozice).
     * 
     * DŮLEŽITÉ: Neukládej zde velká data nebo databázové objekty!
     * Bundle má limit ~500KB.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        
        // Uložení dat do Bundle
        outState.putInt("COUNTER_KEY", counter)
        outState.putString("USER_INPUT", binding.editText.text.toString())
        
        Log.d("MainActivity", "Ukládám stav, counter = $counter")
        
        // Tato data budou dostupná v onCreate() v parametru savedInstanceState
    }
    
    /**
     * onRestoreInstanceState() - Obnovení stavu
     * 
     * Volá se PO onCreate(), pokud bylo savedInstanceState != null
     * Alternativa k obnovování stavu v onCreate()
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        
        // Obnovení dat z Bundle
        counter = savedInstanceState.getInt("COUNTER_KEY", 0)
        val userInput = savedInstanceState.getString("USER_INPUT", "")
        
        // Nastavení do UI
        binding.textView.text = "Počet kliknutí: $counter"
        binding.editText.setText(userInput)
        
        Log.d("MainActivity", "Obnovuji stav v onRestoreInstanceState")
    }
}
```

**Praktické scénáře:**

**Scénář 1: Spuštění aplikace**
```
onCreate() → onStart() → onResume() → [RUNNING]
```

**Scénář 2: Uživatel stiskne Home button**
```
[RUNNING] → onPause() → onStop()
(aktivita v pozadí, ale v paměti)
```

**Scénář 3: Návrat do aplikace z pozadí**
```
onRestart() → onStart() → onResume() → [RUNNING]
```

**Scénář 4: Otočení obrazovky (Configuration Change)**
```
onPause() → onStop() → onDestroy() → onCreate() → onStart() → onResume()
(celá aktivita se ZNOVU VYTVOŘÍ!)
```

**Scénář 5: Přechod na jinou aktivitu**
```
MainActivity: onPause() →
SecondActivity: onCreate() → onStart() → onResume() →
MainActivity: onStop()
```

**Běžné chyby a jak se jim vyhnout:**

❌ **CHYBA 1: Memory leak - držení reference na View**
```kotlin
class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null  // ŠPATNĚ!
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = findViewById(R.id.textView)
    }
    // Po zničení aktivity stále drží referenci na TextView!
}
```

✅ **SPRÁVNĚ: Použij View Binding**
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    // Binding automaticky uvolní reference
}
```

❌ **CHYBA 2: Dlouhá operace v onPause()**
```kotlin
override fun onPause() {
    super.onPause()
    saveLargeDataToDatabase()  // ŠPATNĚ! Blokuje UI thread
}
```

✅ **SPRÁVNĚ: Použij coroutine nebo onStop()**
```kotlin
override fun onStop() {
    super.onStop()
    lifecycleScope.launch {
        saveLargeDataToDatabase()  // Asynchronně v coroutine
    }
}
```

❌ **CHYBA 3: Zapomenutí uložit stav**
```kotlin
// Při otočení obrazovky se data ztratí!
class MainActivity : AppCompatActivity() {
    private var counter = 0
    // Chybí onSaveInstanceState!
}
```

✅ **SPRÁVNĚ: Implementuj ukládání stavu**
```kotlin
class MainActivity : AppCompatActivity() {
    private var counter = 0
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("counter", counter)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        counter = savedInstanceState?.getInt("counter") ?: 0
    }
}
```

---

## 2. Layouty a UI komponenty

### 2.1 Úvod do Layoutů

Layout definuje **strukturu uživatelského rozhraní** (UI) v Android aplikaci. Je to jako "plán domu" - říká, kde se co nachází na obrazovce.

**Dva způsoby vytvoření UI:**
1. **XML soubory** (tradiční, deklarativní) - doporučeno pro začátečníky
2. **Jetpack Compose** (moderní, plně v Kotlinu) - probereme později

**Kde se nacházejí layout soubory:**
```
app/res/layout/
├── activity_main.xml      # Layout pro MainActivity
├── activity_second.xml    # Layout pro SecondActivity  
├── fragment_home.xml      # Layout pro HomeFragment
└── item_user.xml          # Layout pro položku v seznamu (RecyclerView)
```

### 2.2 LinearLayout - Lineární uspořádání

LinearLayout je **nejjednodušší** layout manager. Umísťuje UI elementy za sebe v jedné řadě nebo sloupci.

**Kdy použít LinearLayout:**
- ✅ Jednoduchý formulář (label + input pod sebou)
- ✅ Horizontální řádek tlačítek
- ✅ Vertikální seznam položek (ale pro mnoho položek radši RecyclerView)
- ❌ Komplexní layouty s překrýváním (použij ConstraintLayout nebo FrameLayout)

#### Příklad 1: Vertikální LinearLayout (formulář)

**Umístění:** `app/res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
    LinearLayout - kořenový element
    
    xmlns:android - namespace pro Android atributy
    Je POVINNÝ v každém XML layoutu!
-->
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    
    <!-- ROZMĚRY -->
    android:layout_width="match_parent"    <!-- Šířka: vyplní rodiče (celou šířku obrazovky) -->
    android:layout_height="match_parent"   <!-- Výška: vyplní rodiče (celou výšku obrazovky) -->
    
    <!-- ORIENTACE - směr uspořádání elementů -->
    android:orientation="vertical"         <!-- "vertical" = shora dolů, "horizontal" = zleva doprava -->
    
    <!-- PADDING - vnitřní odsazení (od okrajů layoutu k obsahu) -->
    android:padding="16dp"                 <!-- 16dp odsazení ze všech stran -->
    
    <!-- BACKGROUND - barva pozadí -->
    android:background="@color/white"
    
    <!-- POUZE PRO NÁHLED V EDITORU -->
    tools:context=".MainActivity">         <!-- Říká Android Studiu, která aktivita tento layout používá -->
    
    <!--
        TextView - komponenta pro zobrazení textu
        Podobné jako <label> v HTML nebo Label v desktopových aplikacích
    -->
    <TextView
        android:id="@+id/titleText"        <!-- ID - unikátní identifikátor pro přístup z kódu -->
        android:layout_width="match_parent" <!-- Zabere celou šířku -->
        android:layout_height="wrap_content" <!-- Výška podle obsahu textu -->
        
        <!-- TEXT OBSAH A STYLOVÁNÍ -->
        android:text="@string/login_title"  <!-- Odkazuje se na string z strings.xml (doporučeno!) -->
        <!-- android:text="Přihlášení" -->  <!-- Nebo přímo text (nedoporučeno - lepší externalizovat) -->
        
        android:textSize="24sp"             <!-- Velikost textu v SP (scale-independent pixels) -->
        android:textStyle="bold"            <!-- Styl: normal, bold, italic -->
        android:textColor="@color/black"    <!-- Barva textu -->
        
        <!-- ZAROVNÁNÍ -->
        android:gravity="center"            <!-- Zarovnání textu uvnitř TextView (center, start, end) -->
        
        <!-- MARGIN - vnější odsazení (od tohoto elementu k dalším) -->
        android:layout_marginBottom="24dp"/> <!-- Mezera pod tímto elementem -->
    
    <!--
        EditText - komponenta pro vstup textu od uživatele
        Podobné jako <input> v HTML nebo TextBox v desktopových aplikacích
    -->
    <EditText
        android:id="@+id/usernameInput"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        
        <!-- HINT - placeholder text (zmizí po psaní) -->
        android:hint="@string/username_hint"  <!-- např. "Zadejte uživatelské jméno" -->
        
        <!-- TYP VSTUPU - určuje, jaká klávesnice se zobrazí -->
        android:inputType="text"              <!-- text, textEmailAddress, number, phone, atd. -->
        
        <!-- IKONA -->
        android:drawableStart="@drawable/ic_person" <!-- Ikona vlevo (start = left v LTR jazycích) -->
        android:drawablePadding="8dp"         <!-- Mezera mezi ikonou a textem -->
        
        <!-- STYLING -->
        android:background="@drawable/edittext_background" <!-- Vlastní pozadí (zaoblené rohy atd.) -->
        android:padding="12dp"                <!-- Vnitřní odsazení (text není u okraje) -->
        
        <!-- MAX LENGTH -->
        android:maxLength="50"                <!-- Maximální počet znaků -->
        
        android:layout_marginTop="16dp"/>
    
    <!--
        EditText pro heslo - stejný jako výše, ale s jinými nastaveními
    -->
    <EditText
        android:id="@+id/passwordInput"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="@string/password_hint"
        
        <!-- DŮLEŽITÉ: inputType pro heslo! -->
        android:inputType="textPassword"      <!-- Zobrazí tečky/hvězdičky místo textu -->
        <!-- Další možnosti: textVisiblePassword, numberPassword -->
        
        android:drawableStart="@drawable/ic_lock"
        android:drawablePadding="8dp"
        android:background="@drawable/edittext_background"
        android:padding="12dp"
        android:layout_marginTop="16dp"/>
    
    <!--
        CheckBox - zaškrtávací políčko
    -->
    <CheckBox
        android:id="@+id/rememberMeCheckbox"
        android:layout_width="wrap_content"   <!-- Jen tak velký, jak potřebuje -->
        android:layout_height="wrap_content"
        android:text="@string/remember_me"
        android:textSize="14sp"
        android:layout_marginTop="12dp"/>
    
    <!--
        Button - tlačítko
        Nejpoužívanější interaktivní element
    -->
    <Button
        android:id="@+id/loginButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        
        <!-- TEXT -->
        android:text="@string/login_button"   <!-- např. "Přihlásit se" -->
        android:textSize="16sp"
        android:textStyle="bold"
        android:textColor="@color/white"
        
        <!-- BARVY -->
        android:backgroundTint="@color/primary" <!-- Barva pozadí tlačítka -->
        
        <!-- TVAR -->
        android:padding="14dp"                <!-- Větší tlačítko = lepší UX na mobilu -->
        
        android:layout_marginTop="24dp"
        
        <!-- STAV -->
        android:enabled="true"/>              <!-- false = disabled (šedé, neklikatelné) -->
    
    <!--
        TextView jako odkaz
    -->
    <TextView
        android:id="@+id/forgotPasswordText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/forgot_password"
        android:textColor="@color/primary"
        android:textSize="14sp"
        
        <!-- Podtržení jako odkaz -->
        android:textStyle="bold"
        
        <!-- Zarovnání v rámci rodiče (LinearLayout) -->
        android:layout_gravity="center"       <!-- Vycentruje tento element horizontálně -->
        
        android:layout_marginTop="16dp"
        
        <!-- Klikatelné -->
        android:clickable="true"
        android:focusable="true"
        
        <!-- Ripple efekt při kliknutí -->
        android:background="?attr/selectableItemBackground"/>
    
</LinearLayout>
```

**Vysvětlení klíčových konceptů:**

**1. `layout_width` a `layout_height`:**
```xml
<!-- Tři možnosti: -->
android:layout_width="match_parent"   <!-- Vyplní celou šířku rodiče -->
android:layout_width="wrap_content"   <!-- Jen tak velký, jak je obsah -->
android:layout_width="200dp"          <!-- Pevná velikost (nedoporučeno!) -->
```

**2. Jednotky:**
- **dp** (density-independent pixels) - pro rozměry, padding, margin
  - 1dp = různé px na různých zařízeních
  - Zajišťuje stejnou "fyzickou" velikost na všech obrazovkách
- **sp** (scale-independent pixels) - pro velikost textu
  - Respektuje nastavení velikosti písma v systému
  - Uživatel s horším zrakem může mít větší písmo
- **px** (pixels) - NIKDY NEPOUŽÍVEJ! Vypadá jinak na každém zařízení

**3. Padding vs Margin:**
```
┌─────────────────────────────────┐
│  MARGIN (vnější)                │
│  ┌─────────────────────────┐   │
│  │ BORDER                  │   │
│  │ ┌─────────────────────┐ │   │
│  │ │ PADDING (vnitřní)   │ │   │
│  │ │ ┌─────────────────┐ │ │   │
│  │ │ │  OBSAH          │ │ │   │
│  │ │ └─────────────────┘ │ │   │
│  │ └─────────────────────┘ │   │
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

#### Příklad 2: Horizontální LinearLayout (řádek tlačítek)

**Umístění:** `app/res/layout/button_row.xml`

```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    
    <!-- HORIZONTÁLNÍ orientace -->
    android:orientation="horizontal"
    
    android:padding="16dp"
    
    <!-- Mezera mezi elementy (pouze v LinearLayout!) -->
    android:weightSum="2">                  <!-- Celková váha = 2 (volitelné) -->
    
    <!--
        layout_weight - KLÍČOVÝ atribut pro rozdělení prostoru!
        
        Jak to funguje:
        1. Všechny elementy s weight="0" (nebo bez weight) dostanou svůj prostor
        2. Zbývající prostor se rozdělí podle weight hodnot
        3. Element s weight="1" dostane 1/(suma všech weight) prostoru
    -->
    
    <Button
        android:id="@+id/cancelButton"
        android:layout_width="0dp"            <!-- DŮLEŽITÉ: width="0dp" při použití weight! -->
        android:layout_height="wrap_content"
        android:layout_weight="1"             <!-- Zabere 1 část = 50% -->
        android:text="Zrušit"
        
        <!-- Margins -->
        android:layout_marginEnd="8dp"/>      <!-- Mezera napravo (end = right v LTR) -->
    
    <Button
        android:id="@+id/confirmButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"             <!-- Zabere 1 část = 50% -->
        android:text="Potvrdit"
        
        android:layout_marginStart="8dp"/>    <!-- Mezera nalevo (start = left v LTR) -->
        
</LinearLayout>
```

**Vizualizace layout_weight:**
```
weight="1"        weight="2"
┌─────────┬──────────────────┐
│ Button1 │     Button2      │
│  (33%)  │      (67%)       │
└─────────┴──────────────────┘
```

#### Příklad 3: Použití v Kotlin kódu

**Umístění:** `app/java/com.example.myapp/MainActivity.kt`

```kotlin
package com.example.myapp

// Importy
import android.os.Bundle
import android.widget.Toast  // Pro zobrazení Toast zpráv
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityMainBinding // View Binding třída (generovaná)

/**
 * MainActivity - hlavní aktivita aplikace
 * 
 * Tato aktivita zobrazuje login obrazovku a zpracovává přihlášení uživatele.
 */
class MainActivity : AppCompatActivity() {
    
    /**
     * binding - instance View Binding třídy
     * 
     * View Binding automaticky vytváří property pro každý view s android:id
     * Např: android:id="@+id/loginButton" → binding.loginButton
     * 
     * lateinit = inicializujeme později (v onCreate), ale zajištěně před použitím
     * Pokud použijeme před inicializací, dostaneme UninitializedPropertyAccessException
     */
    private lateinit var binding: ActivityMainBinding
    
    /**
     * onCreate() - inicializační metoda
     * Volá se jako první při vytvoření aktivity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // VŽDY PRVNÍ: zavolat super implementaci
        super.onCreate(savedInstanceState)
        
        // === INICIALIZACE VIEW BINDING ===
        
        // 1. Vytvoření binding instance pomocí inflate()
        //    inflate() načte XML layout a vytvoří Kotlin objekty
        binding = ActivityMainBinding.inflate(layoutInflater)
        //    ↑                                  ↑
        //    Binding třída                      LayoutInflater systémová služba
        //    (generovaná z XML)                 (převádí XML na View objekty)
        
        // 2. Nastavení root view jako obsah aktivity
        setContentView(binding.root)
        //              ↑
        //              root = kořenový LinearLayout z XML
        
        // === PŘÍSTUP K UI ELEMENTŮM ===
        // Díky View Binding máme type-safe přístup k views
        
        // Nastavení textu do TextView
        binding.titleText.text = "Vítejte v aplikaci"
        //      ↑         ↑
        //      ID z XML  property TextView
        
        // === NASTAVENÍ LISTENERŮ ===
        
        // Click listener na tlačítko přihlášení
        binding.loginButton.setOnClickListener {
            // Tento kód se spustí po kliknutí na tlačítko
            
            // Získání textu z EditText
            val username = binding.usernameInput.text.toString()
            //             ↑                     ↑    ↑
            //             ID z XML              Editable objekt
            //                                   Převod na String
            
            val password = binding.passwordInput.text.toString()
            
            // Kontrola, zda uživatel vyplnil údaje
            if (username.isEmpty()) {
                // Nastavení chybové zprávy přímo do EditText
                binding.usernameInput.error = "Zadejte uživatelské jméno"
                //                    ↑
                //                    Zobrazí červený error text pod políčkem
                return@setOnClickListener  // Ukončí listener (nebudeme pokračovat)
            }
            
            if (password.isEmpty()) {
                binding.passwordInput.error = "Zadejte heslo"
                return@setOnClickListener
            }
            
            // Kontrola CheckBoxu
            val rememberMe = binding.rememberMeCheckbox.isChecked
            //                                           ↑
            //                                           Boolean: true/false
            
            // Volání funkce pro přihlášení
            performLogin(username, password, rememberMe)
        }
        
        // Click listener na "Zapomenuté heslo" text
        binding.forgotPasswordText.setOnClickListener {
            // Zobrazení Toast zprávy
            Toast.makeText(
                this,                           // Context (aktivita)
                "Funkce zatím není implementována",  // Zpráva
                Toast.LENGTH_SHORT              // Délka zobrazení (SHORT/LONG)
            ).show()                           // DŮLEŽITÉ: musíme zavolat .show()!
        }
        
        // === PROGRAMOVÉ ZMĚNY UI ===
        
        // Deaktivace tlačítka
        // binding.loginButton.isEnabled = false
        
        // Změna barvy
        // binding.loginButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        
        // Skrytí elementu
        // binding.titleText.visibility = View.GONE  // Úplně zmizí (nezabírá místo)
        // binding.titleText.visibility = View.INVISIBLE  // Neviditelný (zabírá místo)
        // binding.titleText.visibility = View.VISIBLE  // Viditelný (výchozí)
    }
    
    /**
     * Funkce pro provedení přihlášení
     * 
     * @param username Uživatelské jméno
     * @param password Heslo
     * @param rememberMe Zda si pamatovat přihlášení
     */
    private fun performLogin(username: String, password: String, rememberMe: Boolean) {
        // TODO: Zde by byla validace vůči databázi/API
        
        // Pro ukázku jen zobrazíme Toast
        Toast.makeText(
            this,
            "Přihlášení uživatele: $username", // String interpolace v Kotlinu
            Toast.LENGTH_SHORT
        ).show()
        
        // Případně přechod na další aktivitu
        // val intent = Intent(this, HomeActivity::class.java)
        // startActivity(intent)
        // finish()  // Zavře login obrazovku (uživatel se nemůže vrátit)
    }
}
```

**Běžné chyby a řešení:**

❌ **CHYBA: NullPointerException při přístupu k view**
```kotlin
// Starý způsob s findViewById
val button = findViewById<Button>(R.id.loginButton)
button.setOnClickListener { }  // Může být null!
```

✅ **ŘEŠENÍ: Použij View Binding**
```kotlin
// View Binding garantuje non-null
binding.loginButton.setOnClickListener { }  // Nikdy není null
```

❌ **CHYBA: Text přímo v XML**
```xml
<TextView
    android:text="Přihlášení"/>  <!-- Špatně! Nelze překládat -->
```

✅ **ŘEŠENÍ: Použij string resources**
```xml
<!-- strings.xml -->
<string name="login_title">Přihlášení</string>

<!-- layout XML -->
<TextView
    android:text="@string/login_title"/>  <!-- Správně! -->
```

### 2.3 ConstraintLayout - Moderní flexibilní layout

ConstraintLayout je **nejmodernější a nejflexibilnější** layout manager. Umožňuje vytvářet **ploché hierarchie** (flat view hierarchy) bez vnořování, což zrychluje vykreslování.

**Proč ConstraintLayout?**
- ✅ Výkonější než vnořené LinearLayouty
- ✅ Flexibilnější pozicování
- ✅ Responzivní design (adaptuje se na různé velikosti obrazovek)
- ✅ Vizuální editor v Android Studiu
- ❌ Složitější na pochopení než LinearLayout (ale stojí to za to!)

**Základní princip:**
Každý view musí mít alespoň 2 constraints (omezení):
- 1 horizontální (left/right/start/end)
- 1 vertikální (top/bottom)

#### Příklad 1: Základní ConstraintLayout

**Umístění:** `app/res/layout/activity_profile.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--
    ConstraintLayout - kořenový element
    
    Potřebuje speciální namespace pro své atributy:
    xmlns:app = namespace pro ConstraintLayout atributy
-->
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"  <!-- DŮLEŽITÉ pro constraint atributy! -->
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:context=".ProfileActivity">
    
    <!--
        ImageView - zobrazení obrázku
        Umístění: Nahoře uprostřed
    -->
    <ImageView
        android:id="@+id/profileImage"
        android:layout_width="100dp"           <!-- Pevná velikost pro profilový obrázek -->
        android:layout_height="100dp"
        android:src="@drawable/ic_person"      <!-- Zdroj obrázku -->
        android:contentDescription="@string/profile_image_desc"  <!-- Pro accessibility -->
        
        <!-- CONSTRAINTS - definují pozici -->
        
        <!-- Horizontální constraints -->
        app:layout_constraintStart_toStartOf="parent"  <!-- Levá hrana k levé hraně rodiče -->
        app:layout_constraintEnd_toEndOf="parent"      <!-- Pravá hrana k pravé hraně rodiče -->
        <!-- ↑ Tyto dva constraints vycentrují view horizontálně -->
        
        <!-- Vertikální constraint -->
        app:layout_constraintTop_toTopOf="parent"      <!-- Horní hrana k horní hraně rodiče -->
        android:layout_marginTop="32dp"/>              <!-- Plus odsazení -->
    
    <!--
        TextView - jméno uživatele
        Umístění: Pod obrázkem, uprostřed
    -->
    <TextView
        android:id="@+id/nameText"
        android:layout_width="0dp"                <!-- 0dp = MATCH_CONSTRAINT (vyplní dostupný prostor) -->
        android:layout_height="wrap_content"
        android:text="Jan Novák"
        android:textSize="24sp"
        android:textStyle="bold"
        android:gravity="center"                  <!-- Text centrovaný uvnitř TextView -->
        
        <!-- Constraints -->
        app:layout_constraintTop_toBottomOf="@id/profileImage"  <!-- Nad profileImage -->
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="16dp"/>
    
    <!--
        TextView - email
        Umístění: Pod jménem
    -->
    <TextView
        android:id="@+id/emailText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="jan.novak@example.com"
        android:textSize="16sp"
        android:gravity="center"
        
        <!-- Constraints -->
        app:layout_constraintTop_toBottomOf="@id/nameText"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="8dp"/>
    
    <!--
        View - horizontální dělící čára
        Umístění: Pod emailem
    -->
    <View
        android:id="@+id/divider"
        android:layout_width="0dp"
        android:layout_height="1dp"              <!-- Tenká čára -->
        android:background="@color/gray"
        
        <!-- Constraints -->
        app:layout_constraintTop_toBottomOf="@id/emailText"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="24dp"/>
    
    <!--
        TextView - label "Telefon"
        Umístění: Vlevo pod čarou
    -->
    <TextView
        android:id="@+id/phoneLabel"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Telefon:"
        android:textStyle="bold"
        
        <!-- Constraints -->
        app:layout_constraintTop_toBottomOf="@id/divider"
        app:layout_constraintStart_toStartOf="parent"  <!-- Zarovnáno vlevo -->
        android:layout_marginTop="16dp"/>
    
    <!--
        TextView - hodnota telefonu
        Umístění: Napravo od labelu
    -->
    <TextView
        android:id="@+id/phoneValue"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="+420 123 456 789"
        
        <!-- Constraints -->
        app:layout_constraintTop_toTopOf="@id/phoneLabel"     <!-- Zarovnáno s phoneLabel -->
        app:layout_constraintBottom_toBottomOf="@id/phoneLabel"
        <!-- ↑ Tyto dva constraints vertikálně zarovnají s phoneLabel -->
        
        app:layout_constraintStart_toEndOf="@id/phoneLabel"   <!-- Začíná kde končí phoneLabel -->
        app:layout_constraintEnd_toEndOf="parent"             <!-- Táhne se až k pravému okraji -->
        android:layout_marginStart="8dp"/>
    
    <!--
        Button - Upravit profil
        Umístění: Dole u spodního okraje
    -->
    <Button
        android:id="@+id/editButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Upravit profil"
        
        <!-- Constraints -->
        app:layout_constraintBottom_toBottomOf="parent"   <!-- Přilepený k dolnímu okraji -->
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginBottom="16dp"/>
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Vysvětlení constraint typů:**

```
Constraint syntax:
app:layout_constraint[ThisSide]_to[OtherSide]Of="[Target]"
        ↑                ↑            ↑             ↑
    Tato strana    Na stranu      Vztah k      Cílový view
```

**Možné strany:**
- `Start` / `End` - levá/pravá (automaticky se přehazují v RTL jazycích jako arabština)
- `Left` / `Right` - vždy levá/pravá (ignoruje RTL)
- `Top` / `Bottom` - horní/dolní

**Příklady constraints:**
```xml
<!-- Přilepení k rodiči -->
app:layout_constraintTop_toTopOf="parent"      <!-- Horní hrana k hornímu okraji rodiče -->
app:layout_constraintStart_toStartOf="parent"  <!-- Levá hrana k levému okraji rodiče -->

<!-- Vztah k jinému view -->
app:layout_constraintTop_toBottomOf="@id/button"  <!-- Nad spodní hranou button -->
app:layout_constraintStart_toEndOf="@id/image"    <!-- Vlevo od pravé hrany image -->

<!-- Zarovnání se stejnou hranou -->
app:layout_constraintTop_toTopOf="@id/text"       <!-- Horní hrana zarovnaná s text -->
```

#### Příklad 2: Guidelines (pomocné čáry)

Guidelines jsou **neviditelné čáry**, které pomáhají zarovnávat views.

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!--
        Guideline - vertikální čára na 40% šířky
        NEVIDITELNÁ - slouží jen pro zarovnání
    -->
    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"                    <!-- vertikální = svislá čára -->
        app:layout_constraintGuide_percent="0.4"/>        <!-- Na 40% šířky obrazovky -->
        <!-- Alternativy:
             app:layout_constraintGuide_begin="100dp"     - 100dp od začátku
             app:layout_constraintGuide_end="50dp"        - 50dp od konce
        -->
    
    <!--
        ImageView - vlevo od guideline
    -->
    <ImageView
        android:id="@+id/image"
        android:layout_width="0dp"
        android:layout_height="200dp"
        android:src="@drawable/photo"
        android:scaleType="centerCrop"
        
        <!-- Vyplní prostor od začátku po guideline -->
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toStartOf="@id/guideline"  <!-- Končí u guideline -->
        app:layout_constraintTop_toTopOf="parent"/>
    
    <!--
        TextView - vpravo od guideline
    -->
    <TextView
        android:id="@+id/description"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Popis obrázku..."
        android:padding="16dp"
        
        <!-- Vyplní prostor od guideline do konce -->
        app:layout_constraintStart_toEndOf="@id/guideline"  <!-- Začíná u guideline -->
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Vizualizace:**
```
┌─────────────────────────────────┐
│  ┌────────┐│                    │
│  │ Image  ││  Description...    │
│  │        ││                    │
│  └────────┘│                    │
│            ↑ Guideline (40%)    │
└─────────────────────────────────┘
```

#### Příklad 3: Chains (řetězení views)

Chains umožňují **distribuovat více views** rovnoměrně.

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!--
        Tři tlačítka v horizontálním chainu
        
        Chain se vytvoří když:
        1. Views jsou propojené constraints v obou směrech
        2. První view má constraint na rodiče
        3. Poslední view má constraint na rodiče
    -->
    
    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Button 1"
        
        <!-- Constraints pro chain -->
        app:layout_constraintStart_toStartOf="parent"        <!-- 1. Začátek chainu -->
        app:layout_constraintEnd_toStartOf="@id/button2"     <!-- → propojení na button2 -->
        app:layout_constraintTop_toTopOf="parent"
        
        <!-- STYLE CHAINU - určuje, jak se views rozmístí -->
        app:layout_constraintHorizontal_chainStyle="spread"
        <!-- Možnosti:
             spread = rovnoměrně rozmístěné (výchozí)
             spread_inside = kraje u okrajů, mezery uvnitř
             packed = nalepené u sebe uprostřed
        -->
        android:layout_marginTop="100dp"/>
    
    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Button 2"
        
        app:layout_constraintStart_toEndOf="@id/button1"     <!-- ← propojení z button1 -->
        app:layout_constraintEnd_toStartOf="@id/button3"     <!-- → propojení na button3 -->
        app:layout_constraintTop_toTopOf="@id/button1"/>
    
    <Button
        android:id="@+id/button3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Button 3"
        
        app:layout_constraintStart_toEndOf="@id/button2"     <!-- ← propojení z button2 -->
        app:layout_constraintEnd_toEndOf="parent"            <!-- 3. Konec chainu -->
        app:layout_constraintTop_toTopOf="@id/button1"/>
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Vizualizace chain styles:**

**spread (výchozí):**
```
┌────────────────────────────┐
│ [Btn1]  [Btn2]  [Btn3]    │
│   ↑       ↑       ↑        │
│ Rovnoměrné mezery          │
└────────────────────────────┘
```

**spread_inside:**
```
┌────────────────────────────┐
│[Btn1]    [Btn2]    [Btn3]  │
│  ↑         ↑         ↑      │
│ Kraje u okrajů              │
└────────────────────────────┘
```

**packed:**
```
┌────────────────────────────┐
│      [Btn1][Btn2][Btn3]    │
│           ↑                 │
│     Nalepené uprostřed      │
└────────────────────────────┘
```

#### Příklad 4: Bias (vychýlení)

Bias určuje, **kam se view přesune** mezi dvěma constraints.

```xml
<TextView
    android:id="@+id/text"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Text"
    
    <!-- Constraints na obě strany -->
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    
    <!-- BIAS - 0.0 = úplně vlevo, 0.5 = uprostřed (výchozí), 1.0 = úplně vpravo -->
    app:layout_constraintHorizontal_bias="0.3"        <!-- 30% zleva -->
    
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintVertical_bias="0.2"/>        <!-- 20% shora -->
```

**Vizualizace bias:**
```
bias = 0.0        bias = 0.5        bias = 1.0
┌─────────┐      ┌─────────┐      ┌─────────┐
│[View]   │      │  [View] │      │   [View]│
└─────────┘      └─────────┘      └─────────┘
```

#### Použití v Kotlin kódu

**Umístění:** `app/java/com.example.myapp/ProfileActivity.kt`

```kotlin
package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet  // Pro programovou změnu constraints
import com.example.myapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // === PŘÍSTUP K VIEWS ===
        
        // Nastavení dat
        binding.nameText.text = "Jan Novák"
        binding.emailText.text = "jan.novak@example.com"
        binding.phoneValue.text = "+420 123 456 789"
        
        // Načtení obrázku (zde by byla knihovna jako Glide/Coil)
        // Glide.with(this).load(userImageUrl).into(binding.profileImage)
        
        // === CLICK LISTENERY ===
        
        binding.editButton.setOnClickListener {
            // Přechod na edit obrazovku
            // startActivity(Intent(this, EditProfileActivity::class.java))
        }
        
        // === PROGRAMOVÁ ZMĚNA CONSTRAINTS ===
        // Někdy potřebujeme změnit layout za běhu
        
        // Příklad: Přesunout tlačítko nahoru při kliku na obrázek
        binding.profileImage.setOnClickListener {
            changeButtonPosition()
        }
    }
    
    /**
     * Funkce pro programovou změnu constraints
     * Používá ConstraintSet API
     */
    private fun changeButtonPosition() {
        // 1. Vytvoření ConstraintSet instance
        val constraintSet = ConstraintSet()
        
        // 2. Klonování aktuálních constraints z layoutu
        constraintSet.clone(binding.root)  // binding.root = ConstraintLayout
        
        // 3. Odstranění starých constraints
        constraintSet.clear(R.id.editButton, ConstraintSet.BOTTOM)
        
        // 4. Přidání nových constraints
        constraintSet.connect(
            R.id.editButton,              // ID view, který chceme omezit
            ConstraintSet.TOP,            // Strana tohoto view
            R.id.divider,                 // ID cílového view
            ConstraintSet.BOTTOM,         // Strana cílového view
            16                            // Margin v dp
        )
        
        // 5. Aplikování změn na layout
        constraintSet.applyTo(binding.root)
        
        // Volitelně: Přidání animace
        // TransitionManager.beginDelayedTransition(binding.root)
        // constraintSet.applyTo(binding.root)
    }
}
```

**Běžné chyby a řešení:**

❌ **CHYBA: View není viditelný**
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    <!-- CHYBÍ CONSTRAINTS! -->
    android:text="Text"/>
```
**Důvod:** View bez constraints se zobrazí v pozici [0,0] (levý horní roh) a může být překrytý.

✅ **ŘEŠENÍ: Vždy přidej minimálně 2 constraints**
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    android:text="Text"/>
```

❌ **CHYBA: Circular dependency (kruhová závislost)**
```xml
<TextView
    android:id="@+id/text1"
    app:layout_constraintTop_toBottomOf="@id/text2"/>

<TextView
    android:id="@+id/text2"
    app:layout_constraintTop_toBottomOf="@id/text1"/>
<!-- text1 závisí na text2, text2 závisí na text1 = CRASH! -->
```

✅ **ŘEŠENÍ: Jeden view musí být zakotvený k rodiči**
```xml
<TextView
    android:id="@+id/text1"
    app:layout_constraintTop_toTopOf="parent"/>  <!-- Zakotvený k rodiči -->

<TextView
    android:id="@+id/text2"
    app:layout_constraintTop_toBottomOf="@id/text1"/>
```

### 2.3 Práce s UI v Kotlinu

**Základní přístup k views:**
```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Přístup k UI elementům
        val titleText: TextView = findViewById(R.id.titleText)
        val submitButton: Button = findViewById(R.id.submitButton)
        val inputField: EditText = findViewById(R.id.inputField)
        
        // Nastavení textu
        titleText.text = "Nový nadpis"
        
        // Získání textu z EditText
        val userInput = inputField.text.toString()
        
        // Listener na tlačítko
        submitButton.setOnClickListener {
            val text = inputField.text.toString()
            titleText.text = "Zadali jste: $text"
        }
    }
}
```

**View Binding (moderní přístup):**

1. Aktivace v `build.gradle`:
```gradle
android {
    buildFeatures {
        viewBinding true
    }
}
```

2. Použití v kódu:
```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Přímý přístup k views
        binding.titleText.text = "Nový nadpis"
        
        binding.submitButton.setOnClickListener {
            val text = binding.inputField.text.toString()
            binding.titleText.text = "Zadali jste: $text"
        }
    }
}
```

---

## 3. Aktivity a navigace mezi nimi

### 3.1 Co je Activity?
Activity představuje jednu obrazovku v aplikaci. Každá activity má svůj XML layout a Kotlin/Java třídu.

### 3.2 Vytvoření nové Activity

**1. Vytvoření XML layoutu** (`res/layout/second_activity.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <TextView
        android:id="@+id/receivedDataText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Čekám na data..."
        android:textSize="18sp"/>
        
</LinearLayout>
```

**2. Vytvoření Kotlin třídy:**
```kotlin
class SecondActivity : AppCompatActivity() {
    
    private lateinit var binding: SecondActivityBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SecondActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Přijmutí dat z předchozí aktivity
        val receivedData = intent.getStringExtra("DATA_KEY")
        binding.receivedDataText.text = "Obdržená data: $receivedData"
    }
}
```

**3. Registrace v AndroidManifest.xml:**
```xml
<application>
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
    
    <activity 
        android:name=".SecondActivity"
        android:parentActivityName=".MainActivity"/>
</application>
```

### 3.3 Navigace mezi aktivitami

**Jednoduchý přechod:**
```kotlin
// Z MainActivity do SecondActivity
val intent = Intent(this, SecondActivity::class.java)
startActivity(intent)
```

**Přechod s předáním dat:**
```kotlin
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("DATA_KEY", "Nějaká hodnota")
intent.putExtra("NUMBER_KEY", 42)
intent.putExtra("BOOLEAN_KEY", true)
startActivity(intent)
```

**Přijímání dat v cílové aktivitě:**
```kotlin
val stringData = intent.getStringExtra("DATA_KEY")
val numberData = intent.getIntExtra("NUMBER_KEY", 0) // 0 = default hodnota
val booleanData = intent.getBooleanExtra("BOOLEAN_KEY", false)
```

**Předání komplexních objektů (Parcelable):**
```kotlin
// Definice datové třídy
@Parcelize
data class User(
    val name: String,
    val age: Int,
    val email: String
) : Parcelable

// Odeslání
val user = User("Jan Novák", 25, "jan@example.com")
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("USER_KEY", user)
startActivity(intent)

// Příjem
val user = intent.getParcelableExtra<User>("USER_KEY")
```

**Získání výsledku z aktivity (deprecated v novějších verzích):**
```kotlin
// Starý přístup
private val REQUEST_CODE = 1

// Spuštění
val intent = Intent(this, SecondActivity::class.java)
startActivityForResult(intent, REQUEST_CODE)

// Zpracování výsledku
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        val result = data?.getStringExtra("RESULT_KEY")
    }
}
```

**Moderní přístup (Activity Result API):**
```kotlin
class MainActivity : AppCompatActivity() {
    
    // Registrace launcheru
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data?.getStringExtra("RESULT_KEY")
            Toast.makeText(this, "Výsledek: $data", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        
        binding.button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            launcher.launch(intent)
        }
    }
}

// V SecondActivity - vrácení výsledku
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        
        binding.returnButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("RESULT_KEY", "Data zpět")
            setResult(RESULT_OK, resultIntent)
            finish() // Ukončí aktivitu a vrátí se zpět
        }
    }
}
```

---

## 4. Toast a Snackbar

### 4.1 Toast
Toast je jednoduché всплывающее hlášení, které se zobrazí na krátkou dobu.

**Základní použití:**
```kotlin
// Krátké zobrazení (2 sekundy)
Toast.makeText(this, "Toto je toast zpráva", Toast.LENGTH_SHORT).show()

// Dlouhé zobrazení (3.5 sekundy)
Toast.makeText(this, "Delší zpráva", Toast.LENGTH_LONG).show()
```

**Vlastní pozice:**
```kotlin
val toast = Toast.makeText(this, "Zpráva", Toast.LENGTH_SHORT)
toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
toast.show()
```

**Vlastní layout pro Toast (deprecated, ale užitečné):**
```kotlin
val inflater = layoutInflater
val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container))

val text: TextView = layout.findViewById(R.id.text)
text.text = "Vlastní toast"

val toast = Toast(applicationContext)
toast.duration = Toast.LENGTH_LONG
toast.view = layout
toast.show()
```

### 4.2 Snackbar
Snackbar je pokročilejší než Toast - umožňuje akce a zobrazuje se dole na obrazovce.

**Základní použití:**
```kotlin
Snackbar.make(binding.root, "Toto je Snackbar", Snackbar.LENGTH_SHORT).show()
```

**S akcí:**
```kotlin
Snackbar.make(binding.root, "Smazáno", Snackbar.LENGTH_LONG)
    .setAction("VRÁTIT") {
        // Akce při kliknutí na tlačítko
        Toast.makeText(this, "Akce vrácena", Toast.LENGTH_SHORT).show()
    }
    .show()
```

**Vlastní styling:**
```kotlin
val snackbar = Snackbar.make(binding.root, "Zpráva", Snackbar.LENGTH_LONG)
snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.purple_500))
snackbar.setTextColor(Color.WHITE)
snackbar.setActionTextColor(Color.YELLOW)
snackbar.setAction("OK") { 
    // Akce 
}
snackbar.show()
```

**Callback pro události:**
```kotlin
Snackbar.make(binding.root, "Zpráva", Snackbar.LENGTH_LONG)
    .addCallback(object : Snackbar.Callback() {
        override fun onShown(sb: Snackbar?) {
            // Snackbar byl zobrazen
        }
        
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            // Snackbar byl zavřen
            when (event) {
                DISMISS_EVENT_ACTION -> {
                    // Zavřeno akcí
                }
                DISMISS_EVENT_TIMEOUT -> {
                    // Zavřeno timeoutem
                }
            }
        }
    })
    .show()
```

**Kdy použít co:**
- **Toast** - jednoduché informační zprávy, které nevyžadují akci
- **Snackbar** - zprávy s možností akce (např. "Vrátit zpět smazání")

---

## 5. Jetpack Compose

### 5.1 Co je Jetpack Compose?
Jetpack Compose je moderní toolkit pro vytváření UI v Androidu. Místo XML používá deklarativní přístup v Kotlinu.

### 5.2 Základní setup

**build.gradle (app level):**
```gradle
android {
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Compose BOM (Bill of Materials)
    implementation platform('androidx.compose:compose-bom:2023.10.01')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.activity:activity-compose:1.8.0'
    
    debugImplementation 'androidx.compose.ui:ui-tooling'
}
```

### 5.3 Základní Composable funkce

**Jednoduchá Composable funkce:**
```kotlin
@Composable
fun Greeting(name: String) {
    Text(text = "Ahoj, $name!")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting("Alexandře")
}
```

**Activity s Compose:**
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Theme wrapper
            MyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}
```

### 5.4 Základní Compose komponenty

**Text:**
```kotlin
@Composable
fun TextExamples() {
    Column {
        Text("Základní text")
        
        Text(
            text = "Velký tučný text",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Barevný text s centrováním",
            color = Color.Blue,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

**Button:**
```kotlin
@Composable
fun ButtonExamples() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { /* Akce */ }) {
            Text("Základní tlačítko")
        }
        
        OutlinedButton(onClick = { }) {
            Text("Outline tlačítko")
        }
        
        TextButton(onClick = { }) {
            Text("Text tlačítko")
        }
        
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Barevné tlačítko")
        }
    }
}
```

**TextField:**
```kotlin
@Composable
fun TextFieldExample() {
    var text by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Zadejte text") },
            placeholder = { Text("Placeholder") }
        )
        
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Outlined TextField") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### 5.5 Layouty v Compose

**Column (vertikální):**
```kotlin
@Composable
fun ColumnExample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("První položka")
        Text("Druhá položka")
        Text("Třetí položka")
    }
}
```

**Row (horizontální):**
```kotlin
@Composable
fun RowExample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Vlevo")
        Text("Uprostřed")
        Text("Vpravo")
    }
}
```

**Box (překrývání):**
```kotlin
@Composable
fun BoxExample() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(...)
        Text(
            "Text přes obrázkem",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
```

**LazyColumn (scrollovatelný seznam):**
```kotlin
@Composable
fun LazyColumnExample() {
    val items = (1..100).toList()
    
    LazyColumn {
        items(items) { item ->
            Text(
                text = "Položka $item",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
```

### 5.6 State v Compose

**Remember - lokální state:**
```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Počet: $count")
        Button(onClick = { count++ }) {
            Text("Přidat")
        }
    }
}
```

**State hoisting:**
```kotlin
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    
    Column {
        CounterDisplay(count)
        CounterButtons(
            onIncrement = { count++ },
            onDecrement = { count-- }
        )
    }
}

@Composable
fun CounterDisplay(count: Int) {
    Text("Počet: $count")
}

@Composable
fun CounterButtons(onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row {
        Button(onClick = onIncrement) { Text("+") }
        Button(onClick = onDecrement) { Text("-") }
    }
}
```

### 5.7 Modifiers

Modifiers upravují vzhled a chování Composables:

```kotlin
@Composable
fun ModifierExamples() {
    Column {
        Text(
            "Text s modifikátory",
            modifier = Modifier
                .fillMaxWidth()           // Vyplní šířku
                .padding(16.dp)           // Odsazení
                .background(Color.Gray)   // Pozadí
                .border(2.dp, Color.Black) // Rámeček
                .clickable { /* Kliknutí */ } // Klikatelné
        )
        
        Box(
            modifier = Modifier
                .size(100.dp)             // Pevná velikost
                .clip(RoundedCornerShape(8.dp)) // Zaoblené rohy
                .background(Color.Blue)
        )
    }
}
```

---

## 6. Fragmenty

### 6.1 Co je Fragment?
Fragment je znovupoužitelná část UI, která má svůj vlastní lifecycle a může být vložena do aktivity.

### 6.2 Vytvoření fragmentu

**XML layout** (`fragment_first.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <TextView
        android:id="@+id/fragmentTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="První fragment"
        android:textSize="20sp"/>
        
    <Button
        android:id="@+id/navigateButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Přejít na druhý fragment"/>
        
</LinearLayout>
```

**Kotlin třída:**
```kotlin
class FirstFragment : Fragment() {
    
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.navigateButton.setOnClickListener {
            // Navigace na další fragment
            findNavController().navigate(R.id.action_first_to_second)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevence memory leaks
    }
}
```

### 6.3 Fragment Lifecycle

```
onAttach() → onCreate() → onCreateView() → onViewCreated() → 
onStart() → onResume() → RUNNING → onPause() → onStop() → 
onDestroyView() → onDestroy() → onDetach()
```

**Kdy co používat:**
- `onCreate()` - inicializace dat
- `onCreateView()` - vytvoření view
- `onViewCreated()` - nastavení listenerů, práce s UI
- `onDestroyView()` - čištění referencí na views

### 6.4 Vložení fragmentu do aktivity

**Staticky v XML:**
```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/fragment_container"
    android:name="com.example.app.FirstFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

**Dynamicky v kódu:**
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FirstFragment())
                .commit()
        }
    }
}
```

### 6.5 Předávání dat mezi fragmenty

**Bundle argumenty:**
```kotlin
// Vytvoření fragmentu s argumenty
val fragment = SecondFragment()
val bundle = Bundle().apply {
    putString("KEY_NAME", "Jan")
    putInt("KEY_AGE", 25)
}
fragment.arguments = bundle

// Ve fragmentu - přijetí argumentů
class SecondFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val name = arguments?.getString("KEY_NAME")
        val age = arguments?.getInt("KEY_AGE")
    }
}
```

**Pomocí ViewModel (sdílený mezi fragmenty):**
```kotlin
// ViewModel
class SharedViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<String>()
    val selectedItem: LiveData<String> = _selectedItem
    
    fun selectItem(item: String) {
        _selectedItem.value = item
    }
}

// První fragment - odeslání
class FirstFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.button.setOnClickListener {
            viewModel.selectItem("Vybraná položka")
        }
    }
}

// Druhý fragment - příjem
class SecondFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            binding.textView.text = item
        }
    }
}
```

---

## 7. Ukládání dat - SharedPreferences

### 7.1 Co je SharedPreferences?
SharedPreferences je jednoduchý způsob ukládání malých dat (páry klíč-hodnota) lokálně na zařízení.

**Vhodné pro:**
- Uživatelská nastavení
- Poslední stav aplikace
- Jednoduché user preference
- Tokeny, vlajky

**Nevhodné pro:**
- Velká množství dat
- Komplexní objekty
- Citlivá data (hesla) - použít raději Encrypted SharedPreferences

### 7.2 Základní operace

**Získání instance:**
```kotlin
// Privátní SharedPreferences (doporučeno)
val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Nebo pomocí default shared preferences
val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
```

**Ukládání dat:**
```kotlin
val editor = sharedPrefs.edit()
editor.putString("username", "Jan")
editor.putInt("age", 25)
editor.putBoolean("isLoggedIn", true)
editor.putFloat("rating", 4.5f)
editor.putLong("timestamp", System.currentTimeMillis())
editor.apply() // Asynchronní uložení (doporučeno)
// nebo
editor.commit() // Synchronní uložení (blokuje thread)
```

**Kratší zápis:**
```kotlin
sharedPrefs.edit().apply {
    putString("username", "Jan")
    putInt("age", 25)
    putBoolean("isLoggedIn", true)
    apply()
}
```

**Načítání dat:**
```kotlin
val username = sharedPrefs.getString("username", "") // "" = default hodnota
val age = sharedPrefs.getInt("age", 0)
val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
val rating = sharedPrefs.getFloat("rating", 0f)
val timestamp = sharedPrefs.getLong("timestamp", 0L)
```

**Mazání dat:**
```kotlin
// Smazání konkrétního klíče
sharedPrefs.edit().remove("username").apply()

// Smazání všech dat
sharedPrefs.edit().clear().apply()
```

**Kontrola existence klíče:**
```kotlin
if (sharedPrefs.contains("username")) {
    val username = sharedPrefs.getString("username", "")
}
```

### 7.3 Praktický příklad - Login systém

```kotlin
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        
        // Zkontrolovat, jestli už je uživatel přihlášený
        checkLoginStatus()
        
        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            
            if (validateLogin(username, password)) {
                saveLoginData(username)
                navigateToHome()
            }
        }
        
        binding.rememberMeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("rememberMe", isChecked).apply()
        }
    }
    
    private fun checkLoginStatus() {
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val rememberMe = prefs.getBoolean("rememberMe", false)
        
        if (isLoggedIn && rememberMe) {
            val username = prefs.getString("username", "")
            binding.usernameInput.setText(username)
            // Případně rovnou přejít na hlavní obrazovku
        }
    }
    
    private fun saveLoginData(username: String) {
        prefs.edit().apply {
            putBoolean("isLoggedIn", true)
            putString("username", username)
            putLong("lastLoginTime", System.currentTimeMillis())
            apply()
        }
    }
    
    private fun validateLogin(username: String, password: String): Boolean {
        // Validace...
        return true
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
```

### 7.4 Listener na změny

```kotlin
val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
    when (key) {
        "theme" -> {
            // Reagovat na změnu tématu
            applyTheme()
        }
        "language" -> {
            // Reagovat na změnu jazyka
            updateLanguage()
        }
    }
}

// Registrace listeneru
override fun onResume() {
    super.onResume()
    sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
}

// Odregistrování (důležité!)
override fun onPause() {
    super.onPause()
    sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
}
```

### 7.5 Bezpečné ukládání (EncryptedSharedPreferences)

Pro citlivá data používat šifrované SharedPreferences:

```kotlin
// Přidání závislosti do build.gradle
implementation "androidx.security:security-crypto:1.1.0-alpha06"

// Použití
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secret_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

// Použití je stejné jako u normálních SharedPreferences
encryptedPrefs.edit().putString("api_token", "secret_token").apply()
```

---

## 8. Práce s galerií a obrázky

### 8.1 Výběr obrázku z galerie

**Moderní přístup (Activity Result API):**

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    // Launcher pro výběr obrázku
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Obrázek byl vybrán
            binding.imageView.setImageURI(it)
            // Případně uložit URI pro další použití
            selectedImageUri = it
        }
    }
    
    private var selectedImageUri: Uri? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.selectImageButton.setOnClickListener {
            // Spustit výběr obrázku
            pickImageLauncher.launch("image/*")
        }
    }
}
```

### 8.2 Fotografování kamerou

```kotlin
class CameraActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCameraBinding
    private lateinit var photoUri: Uri
    
    // Launcher pro pořízení fotky
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Fotka byla úspěšně pořízena
            binding.imageView.setImageURI(photoUri)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.takePhotoButton.setOnClickListener {
            // Vytvořit soubor pro foto
            photoUri = createImageUri()
            takePictureLauncher.launch(photoUri)
        }
    }
    
    private fun createImageUri(): Uri {
        val image = File(filesDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            image
        )
    }
}
```

**Konfigurace FileProvider v AndroidManifest.xml:**
```xml
<application>
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.provider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
</application>
```

**res/xml/file_paths.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <files-path name="my_images" path="/" />
    <external-path name="external_files" path="." />
</paths>
```

### 8.3 Práce s obrázky - načítání a úpravy

**Načtení obrázku z URI:**
```kotlin
fun loadImageFromUri(uri: Uri) {
    try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        
        binding.imageView.setImageBitmap(bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, "Chyba při načítání obrázku", Toast.LENGTH_SHORT).show()
    }
}
```

**Změna velikosti obrázku:**
```kotlin
fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    
    val ratioBitmap = width.toFloat() / height.toFloat()
    val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
    
    var finalWidth = maxWidth
    var finalHeight = maxHeight
    
    if (ratioMax > ratioBitmap) {
        finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
    } else {
        finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
    }
    
    return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
}
```

**Uložení bitmapy do souboru:**
```kotlin
fun saveBitmapToFile(bitmap: Bitmap, filename: String): File {
    val file = File(filesDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file
}
```

### 8.4 Použití knihovny Glide nebo Coil

**Glide (build.gradle):**
```gradle
implementation 'com.github.bumptech.glide:glide:4.16.0'
```

```kotlin
// Načtení z URL
Glide.with(this)
    .load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(binding.imageView)

// Načtení z URI
Glide.with(this)
    .load(imageUri)
    .circleCrop() // Kruhový obrázek
    .into(binding.imageView)
```

**Coil (modernější, Kotlin-first):**
```gradle
implementation 'io.coil-kt:coil:2.5.0'
```

```kotlin
binding.imageView.load("https://example.com/image.jpg") {
    crossfade(true)
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
    transformations(CircleCropTransformation())
}
```

### 8.5 Práce s oprávněními

**AndroidManifest.xml:**
```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.CAMERA" />
```

**Kontrola a žádost o oprávnění:**
```kotlin
class ImageActivity : AppCompatActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Oprávnění uděleno
            openGallery()
        } else {
            Toast.makeText(this, "Oprávnění odmítnuto", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Oprávnění už máme
                openGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                // Vysvětlit uživateli, proč potřebujeme oprávnění
                showPermissionRationale()
            }
            else -> {
                // Požádat o oprávnění
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }
}
```

---

## 9. Room Database

### 9.1 Co je Room? - Detailní úvod

**Room** je **abstraktní vrstva** (ORM - Object Relational Mapping) nad SQLite databází. SQLite je výchozí databáze v Androidu, ale psaní čistého SQL kódu je náchylné k chybám. Room to řeší.

**Proč Room místo čistého SQLite?**

❌ **Čistý SQLite (bez Room):**
```kotlin
// Musíš psát SQL ručně - riziko překlepů!
val cursor = db.rawQuery("SELECT * FROM usres WHERE id = ?", arrayOf(userId))
//                                       ↑ PŘEKLEP! Runtime chyba!

// Manuální parsing výsledků
if (cursor.moveToFirst()) {
    val id = cursor.getInt(cursor.getColumnIndex("id"))
    val name = cursor.getString(cursor.getColumnIndex("name"))
    // ... spousta boilerplate kódu
}
cursor.close()
```

✅ **S Room:**
```kotlin
// Kotlin interface s anotacemi - compile-time validace!
@Query("SELECT * FROM users WHERE id = :userId")  // Chyby najde už při kompilaci!
suspend fun getUserById(userId: Int): User?        // Type-safe return

// Použití
val user = userDao.getUserById(123)  // Jednoduché, bezpečné
```

**Výhody Room:**
- ✅ **Compile-time validace** SQL dotazů - chyby najdeš už při kompilaci, ne až za běhu
- ✅ **Automatický mapping** mezi SQL a Kotlin objekty
- ✅ **Integrace s LiveData/Flow** - automatické UI update při změně dat
- ✅ **Méně kódu** - anotace místo stovek řádků SQL kódu
- ✅ **Thread safety** - automaticky běží na background threadu
- ✅ **Migration podpora** - snadné verzování databáze

**Tři hlavní komponenty Room:**
1. **Entity** - definuje strukturu tabulky (třída = tabulka, property = sloupec)
2. **DAO** (Data Access Object) - interface s metodami pro přístup k datům
3. **Database** - hlavní přístupový bod, drží instance DAO

```
┌──────────────────────────────────────┐
│        Android Aplikace              │
│  ┌────────────────────────────────┐  │
│  │      ViewModel/Activity        │  │
│  └────────────────────────────────┘  │
│               ↓                       │
│  ┌────────────────────────────────┐  │
│  │        Repository              │  │
│  └────────────────────────────────┘  │
│               ↓                       │
│  ┌────────────────────────────────┐  │
│  │      DAO Interface             │  │
│  │  (Room generuje implementaci)  │  │
│  └────────────────────────────────┘  │
│               ↓                       │
│  ┌────────────────────────────────┐  │
│  │        Room Database           │  │
│  └────────────────────────────────┘  │
│               ↓                       │
│  ┌────────────────────────────────┐  │
│  │      SQLite Database           │  │
│  │  (skutečný soubor na disku)    │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
```

### 9.2 Nastavení Room v projektu

#### Krok 1: Přidání dependencies

**Umístění:** `app/build.gradle.kts` (nebo `build.gradle`)

```gradle
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")  // DŮLEŽITÉ: Annotation processor pro Room
    // nebo v novějších verzích:
    // id("com.google.devtools.ksp")  // KSP je rychlejší než KAPT
}

android {
    // ... ostatní konfigurace
    
    // Pro KSP (novější, rychlejší)
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // === ROOM DEPENDENCIES ===
    val room_version = "2.6.1"  // Aktuální verze k lednu 2025
    
    // Room runtime - základní knihovna
    implementation("androidx.room:room-runtime:$room_version")
    
    // Room KTX - Kotlin extensions (coroutines podpora)
    implementation("androidx.room:room-ktx:$room_version")
    
    // Room compiler - generuje kód
    kapt("androidx.room:room-compiler:$room_version")
    // nebo pro KSP:
    // ksp("androidx.room:room-compiler:$room_version")
    
    // Volitelné: Room testing
    testImplementation("androidx.room:room-testing:$room_version")
    
    // === COROUTINES (potřebné pro suspend funkce) ===
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // === LIFECYCLE (pro LiveData/ViewModel) ===
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
}
```

**Vysvětlení dependencies:**
- `room-runtime` - samotná Room knihovna
- `room-ktx` - přidává Kotlin extension funkce, podporu pro Flow a coroutines
- `room-compiler` - KAPT/KSP plugin, který **generuje kód** na základě anotací
- `room-testing` - utility pro testování databáze

#### Krok 2: Sync Gradle
Po přidání dependencies klikni na **"Sync Now"** v Android Studiu nebo spusť:
```bash
./gradlew build
```

### 9.3 Entity - Definice tabulky

Entity je **datová třída**, která reprezentuje **tabulku v databázi**. Každá instance je řádek, každá property je sloupec.

#### Příklad 1: Základní entity

**Umístění:** `app/java/com.example.myapp/data/User.kt`

```kotlin
package com.example.myapp.data

// === IMPORTY ===
import androidx.room.ColumnInfo      // Pro vlastní názvy sloupců
import androidx.room.Entity           // Označuje třídu jako DB tabulku
import androidx.room.PrimaryKey       // Primární klíč

/**
 * User Entity - reprezentuje tabulku "users" v databázi
 * 
 * @Entity - anotace říká Room, že tato třída je tabulka
 * tableName = "users" - název tabulky v DB (volitelné, výchozí = název třídy)
 */
@Entity(tableName = "users")
data class User(
    /**
     * id - primární klíč tabulky
     * 
     * @PrimaryKey - označuje sloupec jako primární klíč
     * autoGenerate = true - databáze automaticky generuje ID (auto-increment)
     * Když vytvoříme User s id=0, databáze přiřadí skutečné ID
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // Default 0 = databáze vytvoří nové ID
    
    /**
     * name - uživatelské jméno
     * 
     * @ColumnInfo - (volitelné) vlastní název sloupce v DB
     * Kotlin property: name
     * SQL sloupec: user_name
     * 
     * Proč používat? Když chceš jiný název v DB než v Kotlinu
     */
    @ColumnInfo(name = "user_name")
    val name: String,
    
    /**
     * email - emailová adresa
     * Bez @ColumnInfo = název sloupce bude stejný jako property (email)
     */
    val email: String,
    
    /**
     * age - věk uživatele
     */
    val age: Int,
    
    /**
     * createdAt - timestamp vytvoření
     * Default hodnota = aktuální čas při vytvoření objektu
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

**Výsledná SQL tabulka:**
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    user_name TEXT NOT NULL,
    email TEXT NOT NULL,
    age INTEGER NOT NULL,
    created_at INTEGER NOT NULL
);
```

#### Příklad 2: Entity s relacemi (Foreign Key)

**Umístění:** `app/java/com.example.myapp/data/Task.kt`

```kotlin
package com.example.myapp.data

import androidx.room.*

/**
 * Task Entity - úkol přiřazený uživateli
 * 
 * Demonstruje:
 * - Foreign key relaci
 * - Indexy pro rychlejší vyhledávání
 * - Nullable hodnoty
 */
@Entity(
    tableName = "tasks",
    
    /**
     * INDEXY - zrychlují vyhledávání
     * 
     * Index na user_id = rychlejší dotazy typu:
     * SELECT * FROM tasks WHERE user_id = 5
     * 
     * Bez indexu: Full table scan (projde všechny řádky)
     * S indexem: Lookup (okamžitě najde správné řádky)
     */
    indices = [Index(value = ["user_id"])],
    
    /**
     * FOREIGN KEY - relace k jiné tabulce
     * 
     * Zajišťuje referenční integritu:
     * - Nemůžeš vytvořit Task s userId, které neexistuje v users
     * - Když smažeš User, můžeš definovat co se stane s jeho Tasky
     */
    foreignKeys = [
        ForeignKey(
            entity = User::class,          // Rodičovská tabulka
            parentColumns = ["id"],        // Sloupec v User tabulce
            childColumns = ["user_id"],    // Sloupec v Task tabulce
            onDelete = ForeignKey.CASCADE  // Co se stane při smazání uživatele?
            /**
             * CASCADE = smaž i všechny souvisejí Tasky
             * SET_NULL = nastav user_id na null
             * RESTRICT = zakaž smazání pokud existují Tasky
             * NO_ACTION = nic nedělej (může vést k chybám!)
             */
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    /**
     * title - název úkolu
     */
    val title: String,
    
    /**
     * description - popis úkolu
     */
    val description: String,
    
    /**
     * isCompleted - zda je úkol splněný
     * Default false = nové úkoly jsou nesplněné
     */
    val isCompleted: Boolean = false,
    
    /**
     * userId - ID uživatele, kterému úkol patří
     * 
     * Foreign key k users.id
     * Když smažeš uživatele, všechny jeho tasky se smažou (CASCADE)
     */
    @ColumnInfo(name = "user_id")
    val userId: Int,
    
    /**
     * dueDate - datum splnění úkolu
     * 
     * Long? = nullable
     * Timestamp v millisekundách (System.currentTimeMillis())
     * null = úkol nemá termín
     */
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    
    /**
     * priority - priorita úkolu
     * Může být enum, ale ukládá se jako String
     */
    val priority: Priority = Priority.MEDIUM
)

/**
 * Enum pro prioritu
 * Room automaticky konvertuje enum na String pro uložení
 */
enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}
```

**Běžné chyby s Entity:**

❌ **CHYBA: Nemutable properties (var místo val)**
```kotlin
@Entity
data class User(
    @PrimaryKey
    var id: Int,  // ŠPATNĚ! Room vyžaduje immutable (val)
    var name: String
)
```

✅ **SPRÁVNĚ: Immutable properties**
```kotlin
@Entity
data class User(
    @PrimaryKey
    val id: Int,  // DOBŘE! Použij val
    val name: String
)
```

❌ **CHYBA: Nepodporované typy**
```kotlin
@Entity
data class User(
    @PrimaryKey val id: Int,
    val createdAt: Date  // CHYBA! Room nepodporuje java.util.Date přímo
)
```

✅ **ŘEŠENÍ: Type converters nebo Long**
```kotlin
@Entity
data class User(
    @PrimaryKey val id: Int,
    val createdAt: Long  // Použij timestamp (Long)
)

// Nebo vytvoř TypeConverter (později)

#### 9.3.2 DAO (Data Access Object)

```kotlin
@Dao
interface UserDao {
    
    // Základní operace
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long
    
    @Insert
    suspend fun insertAll(users: List<User>)
    
    @Update
    suspend fun update(user: User)
    
    @Delete
    suspend fun delete(user: User)
    
    // Dotazy
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?
    
    @Query("SELECT * FROM users WHERE user_name LIKE :searchQuery")
    fun searchUsers(searchQuery: String): Flow<List<User>>
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Int)
    
    @Query("SELECT * FROM users ORDER BY user_name ASC")
    fun getUsersSortedByName(): Flow<List<User>>
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
```

**Pokročilejší dotazy:**
```kotlin
@Dao
interface TaskDao {
    
    // Dotaz s relací
    @Query("""
        SELECT tasks.*, users.user_name 
        FROM tasks 
        INNER JOIN users ON tasks.user_id = users.id
        WHERE tasks.user_id = :userId
    """)
    fun getTasksForUser(userId: Int): Flow<List<TaskWithUser>>
    
    // Vrácení vlastní datové třídy
    @Query("SELECT user_name, COUNT(*) as taskCount FROM tasks INNER JOIN users ON tasks.user_id = users.id GROUP BY user_id")
    fun getUserTaskCounts(): Flow<List<UserTaskCount>>
    
    // Update s podmínkou
    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Int, completed: Boolean)
    
    // Komplexnější mazání
    @Query("DELETE FROM tasks WHERE isCompleted = 1 AND due_date < :timestamp")
    suspend fun deleteCompletedTasksOlderThan(timestamp: Long)
}

// Pomocné datové třídy
data class TaskWithUser(
    @Embedded val task: Task,
    @ColumnInfo(name = "user_name") val userName: String
)

data class UserTaskCount(
    @ColumnInfo(name = "user_name") val userName: String,
    val taskCount: Int
)
```

#### 9.3.3 Database

```kotlin
@Database(
    entities = [User::class, Task::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

### 9.4 Použití Room v aplikaci

**Repository pattern:**
```kotlin
class UserRepository(private val userDao: UserDao) {
    
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }
    
    suspend fun update(user: User) {
        userDao.update(user)
    }
    
    suspend fun delete(user: User) {
        userDao.delete(user)
    }
    
    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
    
    fun searchUsers(query: String): Flow<List<User>> {
        return userDao.searchUsers("%$query%")
    }
}
```

**ViewModel:**
```kotlin
class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: UserRepository
    val allUsers: LiveData<List<User>>
    
    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        allUsers = repository.allUsers.asLiveData()
    }
    
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insert(user)
    }
    
    fun updateUser(user: User) = viewModelScope.launch {
        repository.update(user)
    }
    
    fun deleteUser(user: User) = viewModelScope.launch {
        repository.delete(user)
    }
    
    fun searchUsers(query: String): LiveData<List<User>> {
        return repository.searchUsers(query).asLiveData()
    }
}
```

**Použití v Activity/Fragment:**
```kotlin
class UserListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUserListBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: UserAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        observeUsers()
        
        binding.addUserButton.setOnClickListener {
            showAddUserDialog()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = UserAdapter(
            onItemClick = { user -> showUserDetail(user) },
            onDeleteClick = { user -> viewModel.deleteUser(user) }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun observeUsers() {
        viewModel.allUsers.observe(this) { users ->
            adapter.submitList(users)
        }
    }
    
    private fun showAddUserDialog() {
        // Dialog pro přidání uživatele
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_user, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.nameInput)
        val emailInput = dialogView.findViewById<EditText>(R.id.emailInput)
        val ageInput = dialogView.findViewById<EditText>(R.id.ageInput)
        
        AlertDialog.Builder(this)
            .setTitle("Přidat uživatele")
            .setView(dialogView)
            .setPositiveButton("Přidat") { _, _ ->
                val user = User(
                    name = nameInput.text.toString(),
                    email = emailInput.text.toString(),
                    age = ageInput.text.toString().toIntOrNull() ?: 0
                )
                viewModel.insertUser(user)
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }
}
```

### 9.5 Migrace databáze

Když měníš strukturu databáze, musíš vytvořit migraci:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Přidání nového sloupce
        database.execSQL("ALTER TABLE users ADD COLUMN phone_number TEXT DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Vytvoření nové tabulky
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS addresses (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                user_id INTEGER NOT NULL,
                street TEXT NOT NULL,
                city TEXT NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)
    }
}

// Použití v databázi
Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
    .build()
```

---

## 10. DataStore

### 10.1 Co je DataStore?
DataStore je modernější náhrada za SharedPreferences. Podporuje asynchronní operace a je bezpečnější.

**Dva typy:**
- **Preferences DataStore** - ukládá páry klíč-hodnota (podobně jako SharedPreferences)
- **Proto DataStore** - ukládá typovaná objekty pomocí Protocol Buffers

### 10.2 Nastavení

**build.gradle:**
```gradle
dependencies {
    implementation "androidx.datastore:datastore-preferences:1.0.0"
}
```

### 10.3 Preferences DataStore

**Vytvoření DataStore:**
```kotlin
// Na úrovni top-level nebo v companion object
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
```

**Definice klíčů:**
```kotlin
object PreferencesKeys {
    val USERNAME = stringPreferencesKey("username")
    val AGE = intPreferencesKey("age")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val THEME = stringPreferencesKey("theme")
}
```

**Repository pro DataStore:**
```kotlin
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    
    // Čtení dat
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                username = preferences[PreferencesKeys.USERNAME] ?: "",
                age = preferences[PreferencesKeys.AGE] ?: 0,
                isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false,
                theme = preferences[PreferencesKeys.THEME] ?: "light"
            )
        }
    
    // Zápis dat
    suspend fun updateUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }
    
    suspend fun updateAge(age: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AGE] = age
        }
    }
    
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }
    
    suspend fun updateTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }
    
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

// Datová třída
data class UserPreferences(
    val username: String,
    val age: Int,
    val isLoggedIn: Boolean,
    val theme: String
)
```

**Použití v Activity:**
```kotlin
class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesRepository: UserPreferencesRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesRepository = UserPreferencesRepository(dataStore)
        
        // Observe preferences
        lifecycleScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                updateUI(prefs)
            }
        }
        
        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                val username = binding.usernameInput.text.toString()
                preferencesRepository.updateUsername(username)
            }
        }
        
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                preferencesRepository.setLoggedIn(false)
                // Navigace na login obrazovku
            }
        }
    }
    
    private fun updateUI(prefs: UserPreferences) {
        binding.usernameInput.setText(prefs.username)
        binding.ageText.text = "Věk: ${prefs.age}"
        
        // Nastavit theme
        when (prefs.theme) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
```

### 10.4 Rozdíly mezi SharedPreferences a DataStore

| Vlastnost | SharedPreferences | DataStore |
|-----------|------------------|-----------|
| Asynchronní | Ne | Ano |
| Bezpečnost vláken | Ne vždy | Ano |
| Chybové stavy | RuntimeException | Try-catch nebo Flow |
| Type safety | Ne | Ano (s Proto) |
| Podpora Flow/LiveData | Ne | Ano |

---

## 11. Cloud databáze (Firebase)

### 11.1 Firebase Firestore

Firestore je NoSQL cloud databáze od Google.

**Nastavení:**

1. Přidat projekt na [Firebase Console](https://console.firebase.google.com)
2. Stáhnout `google-services.json` do `app/`
3. Přidat do build.gradle:

```gradle
// Project level
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}

// App level
plugins {
    id 'com.google.gms.google-services'
}

dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-firestore-ktx'
}
```

**Základní operace:**

```kotlin
// Inicializace
val db = Firebase.firestore

// Přidání dokumentu
val user = hashMapOf(
    "name" to "Jan Novák",
    "email" to "jan@example.com",
    "age" to 25
)

db.collection("users")
    .add(user)
    .addOnSuccessListener { documentReference ->
        Log.d(TAG, "Document added with ID: ${documentReference.id}")
    }
    .addOnFailureListener { e ->
        Log.w(TAG, "Error adding document", e)
    }

// Přidání s vlastním ID
db.collection("users")
    .document("user123")
    .set(user)

// Čtení jednoho dokumentu
db.collection("users")
    .document("user123")
    .get()
    .addOnSuccessListener { document ->
        if (document != null) {
            val name = document.getString("name")
            val age = document.getLong("age")
        }
    }

// Čtení kolekce
db.collection("users")
    .get()
    .addOnSuccessListener { documents ->
        for (document in documents) {
            Log.d(TAG, "${document.id} => ${document.data}")
        }
    }

// Real-time listening
db.collection("users")
    .addSnapshotListener { snapshots, e ->
        if (e != null) {
            Log.w(TAG, "Listen failed", e)
            return@addSnapshotListener
        }
        
        for (doc in snapshots!!) {
            Log.d(TAG, "${doc.id} => ${doc.data}")
        }
    }

// Update
db.collection("users")
    .document("user123")
    .update("age", 26)

// Delete
db.collection("users")
    .document("user123")
    .delete()
```

**Pokročilé dotazy:**

```kotlin
// Filtrování
db.collection("users")
    .whereEqualTo("age", 25)
    .get()

// Komplexnější dotaz
db.collection("users")
    .whereGreaterThan("age", 18)
    .whereLessThan("age", 65)
    .orderBy("age")
    .limit(10)
    .get()

// Compound queries
db.collection("users")
    .whereEqualTo("city", "Prague")
    .whereEqualTo("active", true)
    .get()
```

**Použití datových tříd:**

```kotlin
data class User(
    val name: String = "",
    val email: String = "",
    val age: Int = 0
)

// Zápis
val user = User("Jan Novák", "jan@example.com", 25)
db.collection("users")
    .add(user)

// Čtení
db.collection("users")
    .document("user123")
    .get()
    .addOnSuccessListener { document ->
        val user = document.toObject<User>()
    }
```

---

## 12. Navigation Component

### 12.1 Co je Navigation Component?
Jetpack Navigation je framework pro navigaci mezi fragmenty v aplikaci.

**Výhody:**
- Vizuální editor navigace
- Type-safe argumenty
- Deep linking
- Animace přechodů

### 12.2 Nastavení

**build.gradle:**
```gradle
dependencies {
    val nav_version = "2.7.6"
    
    implementation "androidx.navigation:navigation-fragment-ktx:$nav_version"
    implementation "androidx.navigation:navigation-ui-ktx:$nav_version"
}
```

### 12.3 Vytvoření Navigation Graph

**res/navigation/nav_graph.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">
    
    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.app.HomeFragment"
        android:label="Home"
        tools:layout="@layout/fragment_home">
        
        <action
            android:id="@+id/action_home_to_detail"
            app:destination="@id/detailFragment"
            app:enterAnim="@anim/slide_in_right"
            app:exitAnim="@anim/slide_out_left"
            app:popEnterAnim="@anim/slide_in_left"
            app:popExitAnim="@anim/slide_out_right"/>
    </fragment>
    
    <fragment
        android:id="@+id/detailFragment"
        android:name="com.example.app.DetailFragment"
        android:label="Detail"
        tools:layout="@layout/fragment_detail">
        
        <argument
            android:name="itemId"
            app:argType="integer"/>
            
        <argument
            android:name="itemName"
            app:argType="string"/>
    </fragment>
</navigation>
```

**NavHost v Activity:**
```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph"/>
```

### 12.4 Navigace mezi fragmenty

**Základní navigace:**
```kotlin
// Z HomeFragment do DetailFragment
findNavController().navigate(R.id.action_home_to_detail)
```

**S argumenty:**
```kotlin
// Předání argumentů
val bundle = Bundle().apply {
    putInt("itemId", 123)
    putString("itemName", "Product Name")
}
findNavController().navigate(R.id.action_home_to_detail, bundle)

// Příjem argumentů v cílovém fragmentu
val itemId = arguments?.getInt("itemId")
val itemName = arguments?.getString("itemName")
```

**Safe Args (type-safe):**

1. Přidat plugin do build.gradle (project level):
```gradle
buildscript {
    dependencies {
        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6"
    }
}
```

2. Aplikovat plugin (app level):
```gradle
plugins {
    id 'androidx.navigation.safeargs.kotlin'
}
```

3. Použití:
```kotlin
// Odesílání
val action = HomeFragmentDirections.actionHomeToDetail(
    itemId = 123,
    itemName = "Product Name"
)
findNavController().navigate(action)

// Přijímání
val args: DetailFragmentArgs by navArgs()
val itemId = args.itemId
val itemName = args.itemName
```

**Zpět navigace:**
```kotlin
// Zpět o jeden krok
findNavController().navigateUp()

// Zpět na konkrétní destinaci
findNavController().popBackStack(R.id.homeFragment, false)
```

### 12.5 Bottom Navigation

```xml
<!-- activity_main.xml -->
<androidx.constraintlayout.widget.ConstraintLayout>
    
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottom_nav"
        app:navGraph="@navigation/nav_graph"/>
    
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:menu="@menu/bottom_nav_menu"/>
        
</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        
        bottomNav.setupWithNavController(navController)
    }
}
```

---

## 13. Doporučené postupy a tipy

### 13.1 Architektura (MVVM)

**Rozdělení zodpovědností:**
```
View (Activity/Fragment) 
    ↓
ViewModel
    ↓
Repository
    ↓
Data Source (Room/Retrofit/atd.)
```

### 13.2 Coroutines pro asynchronní operace

```kotlin
// V ViewModelu
viewModelScope.launch {
    try {
        val result = repository.getData()
        _uiState.value = UiState.Success(result)
    } catch (e: Exception) {
        _uiState.value = UiState.Error(e.message)
    }
}
```

### 13.3 LiveData vs Flow

- **LiveData** - jednodušší, lifecycle-aware
- **Flow** - pokročilejší, více operátorů

### 13.4 Dependency Injection

Použít Hilt nebo Koin pro lepší správu závislostí.

### 13.5 Testing

- **Unit testy** - testování logiky (ViewModely, Repository)
- **Instrumentation testy** - testování UI
- **MockK** - mockování v testech

### 13.6 Zabezpečení

- Šifrovat citlivá data
- Používat HTTPS
- Neukládat API klíče přímo v kódu
- Používat ProGuard/R8 pro obfuscation

### 13.7 Performance

- Používat RecyclerView místo ScrollView pro dlouhé seznamy
- Lazy loading obrázků
- Vyhýbat se memory leaks (WeakReference)
- Profiling s Android Profiler

---

## Závěr a tipy pro přípravu na zkoušku

### Jak efektivně používat tento materiál:

#### 1. **První průchod - Pochopení konceptů**
- Projdi si každou kapitolu **v klidu**, čti komentáře v kódu
- Nepřeskakuj vysvětlení - jsou tam důležité detaily
- Dělej si **vlastní poznámky** k částem, které ještě nechápeš

#### 2. **Druhý průchod - Praktické zkoušení**
- Otevři Android Studio a **zkus příklady přepsat sám**
- **Neopisuj** - snaž se napsat z hlavy, koukej jen když nevíš
- Experimentuj - změň hodnoty, přidej další views, rozbij kód aby ses učil z chyb

#### 3. **Třetí průchod - Řešení úkolů**
- Vezmi si konkrétní úkol z GitHub issues
- Nejdřív si **naplánuj**, jak ho budeš řešit
- Použij materiál jako **referenci**, ne jako copy-paste source

### Prioritní témata pro zkoušku:

**🔴 MUST KNOW (základy):**
1. **Activity Lifecycle** - musíš vědět kdy se co volá
2. **Layouts** - LinearLayout a ConstraintLayout
3. **View Binding** - přístup k UI elementům
4. **Intent** - přechod mezi aktivitami, předávání dat

**🟡 SHOULD KNOW (pokročilé):**
5. **Fragmenty** - lifecycle, komunikace
6. **Room Database** - Entity, DAO, Repository pattern
7. **SharedPreferences/DataStore** - ukládání dat
8. **RecyclerView** - seznamy (není v materiálu, ale pravděpodobně v úkolech)

**🟢 NICE TO KNOW (extra body):**
9. **Jetpack Compose** - moderní UI
10. **Navigation Component** - navigace mezi fragmenty
11. **Firebase** - cloud databáze
12. **Coroutines** - asynchronní operace

### Tipy pro řešení úkolů:

**📝 Před začátkem:**
1. Přečti si zadání **3x** - ujisti se, že rozumíš co se po tobě chce
2. Nakresli si **wireframe** (náčrt) UI na papír
3. Rozděl úkol na **menší části**:
   ```
   Příklad: "Aplikace s databází uživatelů"
   ├─ 1. Vytvoř layout (LinearLayout s TextViews)
   ├─ 2. Vytvoř Entity (User data class)
   ├─ 3. Vytvoř DAO (UserDao interface)
   ├─ 4. Vytvoř Database (AppDatabase)
   ├─ 5. Vytvoř Repository
   ├─ 6. Propoj s UI (přidání, zobrazení)
   └─ 7. Testování
   ```

**💻 Během kódování:**
1. **Testuj průběžně** - po každé menší části spusť app
2. **Čti error messages** - Android Studio ti řekne, co je špatně
3. **Používaj Log.d()** - vypisuj hodnoty proměnných:
   ```kotlin
   Log.d("MainActivity", "User ID: $userId")
   ```
4. **Commit často** (pokud používáš Git) - můžeš se vrátit k funkční verzi

**🐛 Když něco nefunguje:**
1. **Přečti si chybovou zprávu** - většinou ti řekne kde je problém
2. **Zkontroluj AndroidManifest.xml** - jsou tam všechny aktivity?
3. **Zkontroluj oprávnění** - potřebuješ CAMERA/STORAGE permission?
4. **Stack Overflow** - pravděpodobně někdo měl stejný problém
5. **Použij debugger** - krokuj kód a sleduj hodnoty proměnných

### Časté chyby začátečníků a jak se jim vyhnout:

❌ **"Aplikace crashuje bez chybové zprávy"**
- Koukni do **Logcat** (dolní panel v Android Studiu)
- Filtruj podle "Error" a názvu tvé aplikace

❌ **"View není viditelný v ConstraintLayout"**
- Zkontroluj že má **2 constraints** (horizontální + vertikální)

❌ **"App se zastaví při otočení obrazovky"**
- Implementuj `onSaveInstanceState()` a `onCreate(savedInstanceState)`
- Nebo použij ViewModel (uchovává data při configuration changes)

❌ **"Room: Cannot access database on main thread"**
- Databázové operace MUSÍ být asynchronní
- Použij `suspend` funkce a volej je z `lifecycleScope.launch { }`

❌ **"lateinit property has not been initialized"**
- Volal jsi `binding = ...` v `onCreate()`?
- Používáš binding před `onCreate()`? To nejde!

### Kontrolní seznam před odevzdáním úkolu:

- [ ] Aplikace **nefailuje** při spuštění
- [ ] Aplikace **nefailuje** při otočení obrazovky
- [ ] Všechny **required features** z zadání fungují
- [ ] **Žádné hardcoded stringy** přímo v layoutu - vše přes `strings.xml`
- [ ] Kód je **čitelný** - logické názvy proměnných, ne `x`, `temp`, `aaa`
- [ ] **Komentáře** u složitých částí (pomůže ti i vyučujícímu)
- [ ] **Žádné warning** v kódu (žluté vlnovky v Android Studiu)
- [ ] Vyzkoušeno na **více velikostech** obrazovek (mobil + tablet)

### Tipy pro efektivní učení Kotlin/Android:

1. **Každý den alespoň 30 minut** - lepší než 5 hodin jednou týdně
2. **Build something** - nejlepší učení je dělat projekty
3. **Čti cizí kód** - otevři open-source Android apps na GitHubu
4. **Nesnažení se pamatovat vše** - použij materiál jako referenci
5. **Ptej se** - na Discordu, Stack Overflow, kolegy

### Zdroje pro prohloubení:

**📚 Oficiální dokumentace:**
- [Android Developers](https://developer.android.com/docs) - nejlepší zdroj
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

**🎓 Online kurzy:**
- [Android Basics in Kotlin](https://developer.android.com/courses/android-basics-kotlin/course) - zdarma od Google
- [Jetpack Compose Pathway](https://developer.android.com/courses/pathways/compose)

**💪 Praktické cvičení:**
- [Android Codelabs](https://codelabs.developers.google.com/?cat=android) - step-by-step tutoriály
- [LeetCode Android](https://leetcode.com/tag/android/) - coding challenges

**👥 Komunita:**
- [r/androiddev](https://reddit.com/r/androiddev) - Reddit komunita
- [Android Discord servery](https://discord.gg/android)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android) - Q&A

**🎬 YouTube kanály:**
- [Philipp Lackner](https://www.youtube.com/@PhilippLackner) - Android tutoriály
- [Coding with Mitch](https://www.youtube.com/@codingwithmitch)

### Poslední rady před zkouškou:

1. **Den před zkouškou:**
   - Projdi si **své poznámky**
   - Obnov si **Activity Lifecycle** diagram
   - Připrav si **cheatsheet** s nejdůležitějšími syntaxemi

2. **Ráno před zkouškou:**
   - **Dobře se vyspi** - unavený mozek = horší výkon
   - Sněz **snídani** - budeš potřebovat energii
   - Přijď **včas** - stres nepomáhá

3. **Během zkoušky:**
   - **Čti zadání pozorně** - 2-3x si ho projdi
   - **Nejdříve jednodušší části** - získáš sebevěru
   - **Nespěchej** - lepší méně funkcí, ale fungujících
   - **Testuj průběžně** - ne všechno nakonec

4. **Když nevíš:**
   - **Použij tyto materiály** jako referenci (pokud jsou povolené)
   - **Vzpomeň si na podobný úkol**, který jsi řešil
   - **Zkus to jinak** - často existuje více řešení

### Co mít po ruce:

📖 **Tento dokument** - kompletní reference
📱 **Android Studio** - IDE pro vývoj
💻 **Emulátor nebo fyzické zařízení** - pro testování
📝 **Poznámky** - tvé vlastní poznámky k jednotlivým tématům
🔍 **Google** - není hanba googlovat, i profesionálové to dělají

---

## Závěrečné slovo

Tento materiál obsahuje **vše co potřebuješ** pro zvládnutí zkoušky z programování mobilních aplikací. Je to **hodně informací**, ale:

✅ **Nemusíš znát vše nazpaměť** - důležité je pochopit koncepty
✅ **Používej jako referenci** - vracej se k částem, které potřebuješ
✅ **Praktikuj** - dělej úkoly, experimentuj, dělej chyby a uč se z nich

**Pamatuj:** Každý programátor začínal od nuly. Důležitý je **konzistentní effort** a **chuť se učit**.

### Hodně štěstí! 🚀

---

## Quick Reference - Rychlá referenční karta

### Activity Lifecycle
```
onCreate() → onStart() → onResume() → [RUNNING]
→ onPause() → onStop() → onDestroy()
```

### View Binding Setup
```kotlin
private lateinit var binding: ActivityMainBinding
binding = ActivityMainBinding.inflate(layoutInflater)
setContentView(binding.root)
```

### Intent (přechod mezi aktivitami)
```kotlin
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("KEY", value)
startActivity(intent)
```

### Room DAO Query
```kotlin
@Query("SELECT * FROM users WHERE id = :id")
suspend fun getUserById(id: Int): User?
```

### Coroutine Launch
```kotlin
lifecycleScope.launch {
    val result = repository.getData()
    binding.textView.text = result
}
```

### SharedPreferences
```kotlin
val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
prefs.edit().putString("key", "value").apply()
val value = prefs.getString("key", "default")
```

---

**Verze materiálu:** 2.0 (Rozšířená s detailními komentáři)  
**Poslední aktualizace:** Leden 2025  

