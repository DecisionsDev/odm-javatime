# ODM Java Time Support Plugin

An Eclipse plugin for IBM Operational Decision Manager (ODM) 9.5+ that adds Java Time API support to rule projects.

## Features

- **Dual Mode Support**: Choose between Compatible and Strict modes
  - **Compatible Mode**: Includes java.util.Date compatibility for legacy projects
  - **Strict Mode**: Pure Java Time API without legacy date support
- **Smart Detection**: Automatically detects java.util.Date usage and recommends appropriate mode
- **Context Menu Integration**: Available on both ODM Rule Projects and BOM folders
- **Duplicate Prevention**: Checks if Java Time support already exists before adding
- **Type-Based Detection**: Uses ODM's native type system for accurate BOM folder identification
- **Organized Templates**: Separate folders for compatible and strict mode templates

## Installation

### Option 1: Dropins Folder
1. Build the plugin JAR
2. Copy to `<ODM_HOME>/eclipse/dropins/`
3. Restart Eclipse/ODM

### Option 2: Eclipse Update Site
1. In Eclipse, go to Help → Install New Software
2. Add the plugin update site
3. Select and install the plugin
4. Restart Eclipse/ODM

## Usage

### Adding Java Time Support to a Project

#### Method 1: From Project Context Menu
1. Right-click on an ODM Rule Project
2. Select "Add Java Time Support"
3. Choose either:
   - **Compatible**: For projects using java.util.Date
   - **Strict**: For projects not using java.util.Date

#### Method 2: From BOM Folder Context Menu
1. Expand the ODM Rule Project
2. Right-click on the "bom" folder
3. Select "Add Java Time Support"
4. Choose the appropriate mode

### Mode Selection Guide

#### When to use Compatible Mode ✅
- Your project already uses java.util.Date
- You need to convert between Date and Java Time types
- You're migrating from legacy date handling
- You have existing rules using Date objects

#### When to use Strict Mode ✅
- New projects without legacy date code
- You want pure Java Time API
- No need for java.util.Date compatibility
- Modern rule development

## What Gets Added

The plugin adds three files to your project's BOM folder:

### Compatible Mode
- `javatime-compatible.voc` - Vocabulary with Date conversion methods
- `javatime-compatible.b2xa` - BOM to XOM mapping with Date converters
- `javatime-compatible.bom` - Business Object Model with compatibility layer

### Strict Mode
- `javatime-strict.voc` - Pure Java Time vocabulary
- `javatime-strict.b2xa` - Direct Java Time mappings
- `javatime-strict.bom` - Pure Java Time Business Object Model

## Java Time API Classes Included

### Core Date/Time Classes (Both Modes)
- **LocalDate**: Date without time zone (e.g., 2024-03-04)
  - Properties: year, month, dayOfMonth
  - Methods: now(), of(), plusDays(), minusDays(), isAfter(), isBefore()
  
- **LocalTime**: Time without date or time zone (e.g., 14:30:00)
  - Properties: hour, minute, second
  - Methods: now(), of(), plusHours(), minusHours()
  
- **LocalDateTime**: Date and time without time zone (e.g., 2024-03-04T14:30:00)
  - Properties: year, month, dayOfMonth, hour, minute
  - Methods: now(), of(), toLocalDate(), toLocalTime(), plusDays(), minusDays()

### Additional Classes (Strict Mode)
- **Duration**: Time-based amount (hours, minutes, seconds)
  - Methods: ofDays(), ofHours(), ofMinutes(), toHours(), toMinutes()
  
- **Period**: Date-based amount (years, months, days)
  - Methods: ofDays(), ofWeeks(), ofMonths(), ofYears()
  
- **Month**: Enum for months (JANUARY, FEBRUARY, etc.)
- **DayOfWeek**: Enum for days of the week (MONDAY, TUESDAY, etc.)

### Compatibility Classes (Compatible Mode Only)
- **DateConverter**: Utility for converting between java.util.Date and Java Time types
  - `toLocalDate(Date)`: Convert Date to LocalDate
  - `toLocalDateTime(Date)`: Convert Date to LocalDateTime
  - `toDate(LocalDate)`: Convert LocalDate to Date
  - `toDate(LocalDateTime)`: Convert LocalDateTime to Date

## Rule Examples

### Example 1: Using LocalDate (Both Modes)
```
if the birth date of the customer is before 1990-01-01
then set the customer category to "Legacy Customer"
```

### Example 2: Date Arithmetic (Both Modes)
```
set the expiry date to the start date plus 30 days
if today is after the expiry date
then set the status to "Expired"
```

### Example 3: Date Conversion (Compatible Mode Only)
```
set the local date to the local date from the legacy date
if the local date is after 2020-01-01
then set the status to "Recent"
```

### Example 4: Using Duration (Strict Mode Only)
```
set the processing time to 2 hours
if the elapsed time is greater than the processing time
then set the alert to "Timeout"
```

## Requirements

- IBM ODM 9.5 or higher
- Eclipse with ODM plugins installed
- Java 11 or higher

## Building from Source

### Prerequisites
- Eclipse PDE (Plugin Development Environment)
- ODM 9.5+ SDK or installation

### Build Steps
1. Import the plugin project into Eclipse
2. Configure target platform with ODM bundles
3. Right-click project → Export → Deployable plug-ins and fragments
4. Select destination directory
5. The plugin JAR will be generated

## Project Structure

```
com.ibm.odm.javatime.plugin/
├── META-INF/
│   └── MANIFEST.MF                    # Plugin manifest
├── plugin.xml                          # Extension points configuration
├── build.properties                    # Build configuration
├── README.md                           # This file
├── src/com/ibm/odm/javatime/
│   ├── Activator.java                 # Plugin activator
│   ├── handlers/
│   │   ├── AddCompatibleHandler.java  # Compatible mode handler
│   │   └── AddStrictHandler.java      # Strict mode handler
│   └── utils/
│       ├── BomFileManager.java        # File operations
│       ├── DateUsageDetector.java     # Date usage detection
│       └── ProjectHelper.java         # Project utilities
└── resources/templates/
    ├── compatible/                     # Compatible mode templates
    │   ├── javatime-compatible.voc
    │   ├── javatime-compatible.b2xa
    │   └── javatime-compatible.bom
    └── strict/                         # Strict mode templates
        ├── javatime-strict.voc
        ├── javatime-strict.b2xa
        └── javatime-strict.bom
```

## Vocabulary Translation System

The plugin includes a comprehensive translation system for generating localized vocabulary files (.voc) in multiple languages.

### Translation Architecture

The system uses YAML-based translation files with Python scripts to generate vocabulary files:

```
resources/templates/
├── generate_generic_template.py    # Creates generic translation template
├── translate_voc.py                # Generates VOC files from translations
├── update_french_yaml.py           # Updates French YAML from template
├── translations_xx_XX.yaml         # Generic template (270 strings)
├── translations_fr_FR.yaml         # French translations
├── translation_keys_generic.txt    # Reference for @TRxxx keys
├── compatible/                     # English VOC files (compatible mode)
│   ├── javatime-compatible_en_US.voc
│   └── javatime-compatible_fr_FR.voc
└── strict/                         # English VOC files (strict mode)
    ├── javatime-strict_en_US.voc
    └── javatime-strict_fr_FR.voc
```

### Generating a Generic Translation Template

To create a translation template for a new locale:

```bash
cd resources/templates
python generate_generic_template.py
```

This generates:
- `translations_xx_XX.yaml` - Template with 270 translatable strings
- `translation_keys_generic.txt` - Reference mapping @TR001-@TR270 to English text

**What gets excluded from translation:**
- `.precedence` - Technical precedence values
- `#sortIndex` - Numeric sort indices (0-11)
- `#predicate.group` - Predicate grouping values (1-2)
- `#predicate.*` - All predicate operators (>, <, >=, <=, etc.)

### Creating a Locale-Specific Translation File

#### Step 1: Copy the Generic Template

```bash
cp translations_xx_XX.yaml translations_es_ES.yaml
```

#### Step 2: Update Locale Metadata

Edit the new file and update:
```yaml
locale: es_ES              # Change from xx_XX to your locale
time_prefix: tiempo.       # Localized prefix for time-related terms
```

**Common time prefixes:**
- English: `time.`
- French: `temps.`
- Spanish: `tiempo.`
- German: `zeit.`
- Dutch: `tijd.`
- Portuguese: `tempo.`
- Italian: `tempo.`
- Japanese: `時間.`
- Korean: `시간.`
- Chinese (Simplified): `时间.`
- Chinese (Traditional): `時間.`
- Polish: `czas.`
- Norwegian: `tid.`

#### Step 3: Translate the Strings

Replace `@TRxxx` keys with translations in your language:

```yaml
# Before:
'April': '@TR001'
'August': '@TR002'

# After (Spanish):
'April': 'Abril'
'August': 'Agosto'
```

**Important:**
- Keep placeholders like `{0}`, `{1}`, `{this}` unchanged
- Maintain the same structure and formatting
- Use single quotes around both keys and values

#### Step 4: Generate VOC Files

Once translations are complete (or partially complete):

```bash
python translate_voc.py translations_es_ES.yaml
```

This generates two VOC files:
- `compatible/javatime-compatible_es_ES.voc` - With localized prefix (tiempo.)
- `strict/javatime-strict_es_ES.voc` - Without prefix

### Translation Workflow Example

**Complete workflow for adding Spanish translations:**

```bash
# 1. Navigate to templates directory
cd resources/templates

# 2. Create Spanish translation file from template
cp translations_xx_XX.yaml translations_es_ES.yaml

# 3. Edit translations_es_ES.yaml
#    - Change locale to es_ES
#    - Change time_prefix to tiempo.
#    - Replace @TRxxx keys with Spanish translations

# 4. Generate VOC files
python translate_voc.py translations_es_ES.yaml

# 5. Verify generated files
ls -l compatible/javatime-compatible_es_ES.voc
ls -l strict/javatime-strict_es_ES.voc
```

### How the Translation Script Works

The `translate_voc.py` script:

1. **Reads the YAML file** with locale, time_prefix, and translations
2. **Loads the English VOC template** (strict mode)
3. **Replaces English strings** with translated strings
4. **Handles time prefix intelligently:**
   - **Compatible mode**: If English has "time." prefix, removes it, translates base text, adds localized prefix
   - **Strict mode**: Uses translations as-is (no prefix)
5. **Generates both VOC files** (compatible and strict modes)

**Example prefix handling:**

```yaml
# English VOC (compatible mode):
com.ibm.odm.time.Date#label = time.date & time

# French translation YAML:
'date & time': 'date et heure'

# Generated French VOC (compatible mode):
com.ibm.odm.time.Date#label = temps.date et heure

# Generated French VOC (strict mode):
com.ibm.odm.time.Date#label = date et heure
```

### Updating Existing Translations

To update an existing translation file with new strings from the template:

```bash
python update_french_yaml.py
```

This script:
- Preserves existing translations
- Adds new @TRxxx keys for untranslated strings
- Updates the progress counter

### Translation Progress Tracking

Each translation YAML file includes a progress header:

```yaml
# French translations for Java Time BOM
# Progress: 143/270 translations completed (52.9%)
# Replace @TRxxx keys with French translations
```

To check progress:
```bash
grep -c "@TR" translations_fr_FR.yaml  # Count remaining @TRxxx keys
```

### Supported Locales

The system supports any locale. Common locales for ODM:

Locale | Language | Time Prefix | Status |
|--------|----------|-------------|--------|
en_US | English | time. | ✅ Complete |
fr_FR | French | temps. | 🔄 In Progress |
es_ES | Spanish | tiempo. | ⏳ Pending |
de_DE | German | zeit. | ⏳ Pending |
nl_NL | Dutch | tijd. | ⏳ Pending |
pt_BR | Portuguese | tempo. | ⏳ Pending |
it_IT | Italian | tempo. | ⏳ Pending |
ja_JP | Japanese | 時間. | ⏳ Pending |
ko_KR | Korean | 시간. | ⏳ Pending |
zh_CN | Chinese (Simplified) | 时间. | ⏳ Pending |
zh_TW | Chinese (Traditional) | 時間. | ⏳ Pending |
pl_PL | Polish | czas. | ⏳ Pending |
no_NO | Norwegian | tid. | ⏳ Pending |

### Translation Reference

Use `translation_keys_generic.txt` to see what each @TRxxx key represents:

```
@TR001: April
@TR002: August
@TR003: Checks whether a time point is after a time period.
...
```

### Best Practices for Translation

1. **Use native speakers** for accurate translations
2. **Maintain consistency** with ODM terminology
3. **Test generated VOC files** in ODM environment
4. **Keep placeholders intact** ({0}, {1}, {this})
5. **Follow locale conventions** for date/time terminology
6. **Review technical terms** (may not need translation)
7. **Update progress regularly** as you translate

### Troubleshooting Translation Issues

**Script errors:**
- ✓ Ensure Python 3.x is installed
- ✓ Install PyYAML: `pip install pyyaml`
- ✓ Check YAML syntax (proper quoting, indentation)

**Missing translations:**
- ✓ Verify all @TRxxx keys are replaced
- ✓ Check for typos in translation keys
- ✓ Ensure locale and time_prefix are set correctly

**VOC file issues:**
- ✓ Validate generated VOC files in ODM
- ✓ Check for special characters encoding
- ✓ Verify prefix handling (compatible vs strict)

## Smart Detection Feature

The plugin automatically detects java.util.Date usage in your project:

### Detection Process
1. Scans existing BOM files (.voc, .b2xa, .bom)
2. Looks for java.util.Date references
3. Recommends appropriate mode based on findings

### User Warnings
- **Compatible mode** selected but no Date usage detected → Suggests Strict mode
- **Strict mode** selected but Date usage detected → Warns about compatibility issues
- User can proceed with their choice or cancel

## Troubleshooting

### Menu doesn't appear
- ✓ Verify you're right-clicking on an ODM Rule Project (not a regular Java project)
- ✓ Check that the project has the ODM nature
- ✓ Ensure the plugin is properly installed

### "BOM Folder Not Found" error
- ✓ Verify the project has a "bom" folder
- ✓ Check that the folder is properly configured in the project metadata
- ✓ Ensure the project structure is valid

### "Already exists" message
- ✓ Java Time support has already been added to this project
- ✓ Check the bom folder for existing javatime-*.voc/b2xa/bom files
- ✓ Remove existing files if you want to re-add with different mode

### Template files not found
- ✓ Ensure the plugin JAR includes the resources/templates directory
- ✓ Check build.properties includes resources/ in bin.includes
- ✓ Verify template files are in correct folders (compatible/ and strict/)

### Compilation errors after adding BOM
- ✓ Refresh the project (F5 or right-click → Refresh)
- ✓ Clean and rebuild the project
- ✓ Check that ODM bundles are properly configured

## Best Practices

### For New Projects
1. Use **Strict mode** for clean, modern Java Time API usage
2. Avoid java.util.Date entirely
3. Use LocalDate for dates, LocalTime for times, LocalDateTime for timestamps

### For Legacy Projects
1. Use **Compatible mode** to maintain existing Date functionality
2. Gradually migrate Date usage to Java Time types
3. Use DateConverter for transitional code

### General Guidelines
- Choose the mode based on your project's actual needs
- Don't mix Date and Java Time unnecessarily
- Use the smart detection feature to guide your choice
- Test thoroughly after adding Java Time support

## FAQ

**Q: Can I switch from Compatible to Strict mode later?**  
A: Yes, but you'll need to remove the existing BOM files first and update any rules using Date conversion methods.

**Q: Will this affect my existing rules?**  
A: No, adding Java Time support only adds new capabilities. Existing rules remain unchanged.

**Q: Can I customize the BOM templates?**  
A: Yes, you can modify the template files in the plugin's resources/templates folder before building.

**Q: Does this work with ODM Decision Center?**  
A: Yes, once added to a rule project, the Java Time BOM is available in Decision Center.

**Q: What if I need both modes?**  
A: You can only have one mode active at a time. Choose the mode that best fits your primary use case.

## License

Copyright IBM Corporation

## Support

For issues or questions:
- Check the troubleshooting section above
- Refer to IBM ODM documentation
- Contact your ODM support team

## Version History

### 1.0.0 (Initial Release)
- Dual mode support (Compatible/Strict)
- Smart java.util.Date detection with user warnings
- Context menu integration for projects and BOM folders
- Type-based BOM folder detection
- Comprehensive Java Time API coverage
- Organized template structure in separate folders
- Duplicate prevention
- Comprehensive error handling and user feedback

## Contributing

To contribute to this plugin:
1. Follow the Eclipse plugin development guidelines
2. Maintain compatibility with ODM 9.5+
3. Test thoroughly with both Compatible and Strict modes
4. Update documentation for any new features

## Acknowledgments

Built for IBM Operational Decision Manager to modernize date/time handling in business rules using the Java Time API (JSR-310).