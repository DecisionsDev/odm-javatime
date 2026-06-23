# Java time support for IBM Operational Decision Manager

## Introduction

Introduced in Java 8, the `java.time` package defines key date-time concepts such as instants, durations, dates, times, time zones, and periods. This guide explains how to use `java.time` as a replacement for `java.util.Date` within IBM Operational Decision Manager.

- [Java time support for IBM Operational Decision Manager](#java-time-support-for-ibm-operational-decision-manager)
  - [Introduction](#introduction)
  - [Working on existing projects](#working-on-existing-projects)
  - [Starting new projects](#starting-new-projects)
  - [Using the Eclipse plugin](#using-the-eclipse-plugin)
    - [Install the plugin](#install-the-plugin)
    - [Use the plugin](#use-the-plugin)
    - [When to use the plugin vs distributions](#when-to-use-the-plugin-vs-distributions)
  - [Vocabulary languages](#vocabulary-languages)
  - [Building the Solution](#building-the-solution)
    - [Prerequisites](#prerequisites)
    - [Build Steps](#build-steps)
    - [Build Outputs](#build-outputs)
- [Issues and contributions](#issues-and-contributions)
- [License](#license)
- [Notice](#notice)



## Working on existing projects

The `java.util.Date` class and `java.time` APIs can interoperate, enabling a smooth migration path from the legacy date system to the modern Java 8 `java.time` package. The `javatime-compatible-1.0.0-distribution.zip` has been designed for gradual adoption, with the option to eventually phase out `java.util.Date` entirely.

Here are the various steps to follow:

1. unzip `javatime-compatible-1.0.0-distribution.zip`
2. import the projects into your Rule Designer workspace

The following projects have been added:

| Project Name | Description |
| --- | --- |
| JavaTimeModelCompatible | This BOM project includes classes and the vocabulary for the `java.time` package. The verbalization of `java.time` types is designed to avoid conflicts with existing `java.util.Date` definitions. |
| SampleXomCompatible | This Java project demonstrates how to work with the `java.util.Date` and `java.time` package. |
| SampleRulesetCompatible | This Rule project shows how to use both `java.util.Date` and `java.time` in rules and decision tables. |

> [!IMPORTANT]
> To allow `java.util.Date` and `java.time` package to coexist, all `java.time` verbalizations use a _time._ prefix. For example, _date & time_ refers to the `java.util.Date` version, while _time.date & time_ refers to its `java.time` equivalent. In addition, literal values for `java.time` are prefixed by #.

The following new types are available:

| Name | Verbalization |
| --- | --- | 
| CalendarDuration | time.calendar duration |
| Date | time.date & time |
| SimpleDate |time.date | 
| UniversalDate | time.universal date & time |
| Time | time.time |
| Year | time.year |
| DayOfWeek | time.day of week (time.Monday...) | 
| Month | time.month (time.January...) |
| TimePeriod | time.time period |

Conversion methods have been added to support interoperability between `java.util.Date` and `java.time` types. These methods are prefixed with _as_. For example, `{0} as time.date & time in {1}` converts a `java.util.Date` to a `java.time.ZonedDateTime`, while `{0} as date` performs the reverse conversion.

Example: the following rule demonstrates how to convert a `java.util.Date` to a `java.time.ZonedDateTime` to check if it matches a customer's birthday.

``` LUA
definitions
  set 'birth date' to the birth date of 'the customer' as time.date & time in "Europe/Paris"; 
  set 'today' to now as time.date & time in "Europe/Paris"; 
if 'birth date' is in the same calendar month as today and
   'birth date' is on the same calendar day as today 
then 
	print "Today " + the date of today + " is your birthday, happy birthday !!!" ;
```

## Starting new projects

For new projects, you can rely exclusively on the `java.time` package without involving `java.util.Date`. In this setup, `java.time` serves as the standard for handling dates and times in rules and decision tables.

Here are the various steps to follow:

1. unzip `javatime-strict-1.0.0-distribution.zip`
2. import the projects into your Rule Designer workspace

The following projects have been added:

| Project Name | Description |
| --- | --- |
| JavaTimeModelStrict | This BOM project includes classes and the vocabulary for the `java.time` package. The verbalization of `java.time` types are compatible with IBM Automation Decision Services (ADS) and IBM Decision Intelligence (DI). |
| SampleXomStrict | This Java project demonstrates how to work with the `java.time` package. |
| SampleRulesetStrict | This Rule project shows how to use `java.time` in rules and decision tables. |

The following new types are available:

| Name | Verbalization |
| --- | --- | 
| CalendarDuration | calendar duration |
| Date | date & time |
| SimpleDate | date | 
| UniversalDate | universal date & time |
| Time | time |
| Year | year |
| DayOfWeek | day of week (Monday...) | 
| Month | month (January...) |
| TimePeriod | time period |

Example: the following rule demonstrates how to compute the age of a given customer.

``` LUA
definitions
  set 'age' to the number of years between now and the birth date of 'the customer';
then
  print "You are " + age + " years old.";
```

## Using the Eclipse plugin

The Eclipse plugin provides another way to use the Java Time extension in Rule Designer. Instead of importing the sample distribution projects manually, you can install the plugin and let it add the Java Time BOM files directly into an existing ODM rule project.

### Install the plugin

The plugin is packaged as an Eclipse update site.

1. Obtain the versioned update site archive from [`odm-javatime/distrib`](odm-javatime/distrib)
2. In Eclipse / Rule Designer, open **Help** → **Install New Software...**
3. Add the update site archive
4. Select the ODM Java Time feature and complete the installation
5. Restart Eclipse when prompted

### Use the plugin

After installation:

1. Open an ODM Rule Designer workspace
2. Right-click an ODM Rule Project or its BOM folder
3. Select **Add Java Time Support**
4. Choose one of the two modes:
   - **Compatible mode**: use this for existing projects that already rely on `java.util.Date`
   - **Strict mode**: use this for new projects that use only `java.time`
5. Let the plugin copy the BOM resources into the project

The plugin automatically:
- detects existing `java.util.Date` usage when relevant
- recommends the appropriate mode
- copies the required `.voc`, `.bom`, and `.b2xa` files
- generates project-specific UUID values for the imported BOM files

### When to use the plugin vs distributions

Use the distribution ZIPs when you want complete sample projects that you can import into a workspace.

Use the Eclipse plugin when you want to enable Java Time support directly inside an existing ODM rule project without importing the sample projects.

## Vocabulary languages

The provided vocabularies are available only for English and French.

- English vocabularies use the `_en_US.voc` suffix
- French vocabularies use the `_fr_FR.voc` suffix

If you need another language:

1. Copy the English vocabulary file
2. Rename it with the target 4-letter locale suffix, for example [`_de_DE.voc`](odm-javatime/README.md) or [`_es_ES.voc`](odm-javatime/README.md)
3. Update the `uuid` value in the copied VOC so that the new vocabulary has its own UUID
4. Translate the vocabulary text while keeping the technical structure and identifiers consistent with the original file

This applies whether you use the distribution projects or the Eclipse plugin templates. Follow the step below to update the plugin and the distribution zip.

Create a pull request with your changes and we'll review and merge it for others to use.


## Building the Solution

### Prerequisites

- **Java 21** or later
- **Apache Maven 3.6+**
- **IBM ODM 9.6** or later (for Eclipse plugin build)

### Build Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd odm-javatime
   ```

2. **Choose what to build using Maven profiles**

   The project provides Maven profiles to control what gets built:

   **Build only distributions:**
   ```bash
   mvn clean install -Pdistribution
   ```
   Builds: Compatible and Strict distributions

   **Build only Eclipse plugin (requires odm.home):**
   ```bash
   mvn clean install -Pplugin -Dodm.home=/path/to/your/odm/installation
   ```
   Builds: Eclipse plugin, feature, and update site
   
   > **Note**: The `odm.home` path should point to the root directory of your ODM installation (e.g., `C:\Program Files\IBM\ODM96` on Windows or `/opt/IBM/ODM96` on Linux). The build uses `${odm.home}/plugins` to access ODM Eclipse plugins required for compilation.

   **Build everything (distributions + plugin):**
   ```bash
   mvn clean install -Pall -Dodm.home=/path/to/your/odm/installation
   ```
   Builds: All distributions and Eclipse plugin

   **Build with no profile (integration tests only):**
   ```bash
   mvn clean install
   ```
   Builds: Integration tests only (useful for developement only)

### Build Outputs

After a successful build, you'll find:

| Artifact | Location | Description |
| --- | --- | --- |
| Compatible Distribution | `javatime-compatible/target/javatime-compatible-1.0.0-distribution.zip` | ZIP containing JavaTimeModelCompatible and sample projects for gradual migration |
| Strict Distribution | `javatime-strict/target/javatime-strict-1.0.0-distribution.zip` | ZIP containing JavaTimeModelStrict and sample projects for new projects |
| Eclipse Update Site | `eclipse-plugin/JavaTimeUpdate/target/com.ibm.odm.javatime.updatesite-1.0.0.zip` | Eclipse plugin update site archive for installation in Rule Designer |


# Issues and contributions
For issues relating specifically to the Dockerfiles and scripts, please use the [GitHub issue tracker](../../issues).
We welcome contributions following [our guidelines](CONTRIBUTING.md).

# License
The Dockerfiles and associated scripts found in this project are licensed under the [Apache License 2.0](LICENSE).

# Notice
© Copyright IBM Corporation 2026.