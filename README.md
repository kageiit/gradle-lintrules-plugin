gradle-lintrules-plugin
=======================

Lintrules is a [Gradle](https://www.gradle.org) plugin that allows you to:
- Run custom lint rules on android application projects.
- Bundle custom lint rules with AARs.

Installation
------------

To use the plugin with Gradle 2.1 or later, add the following to your `build.gradle`:

```groovy
plugins {
  id 'com.kageiit.lintrules' version '1.1.0'
}
```

To use the plugin with Gradle 2.0 or older, add the following to `build.gradle`:

```groovy
buildscript {
  repositories {
    maven {
      url 'https://plugins.gradle.org/m2/'
    }
  }
  dependencies {
    classpath 'gradle.plugin.com.kageiit:lintrules:1.+'
  }
}
```

Usage
-----

Assuming that your custom lint rules are in a project named `lint`, you can apply them to an application project or bundle them with the AAR of a library project like so:

```groovy
apply plugin: 'com.android.application' // or apply plugin: 'com.android.library'
apply plugin: 'com.kageiit.lintrules'

dependencies {
  lintRules project(':lint')
}
```

License
-------

    Copyright 2015 Gautam Korlam

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
