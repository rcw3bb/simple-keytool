# Simple Keytool Gradle Plugin

The plugin that allows you access to keytool commands inside gradle as task.

# Pre-requisite

* Java 11
* Windows 

## Plugging in the simple-keytool

In your **build.gradle** file add the following plugin:

```groovy
plugins {
    id "xyz.ronella.simple-keytool" version "1.0.0"
}
```

> A **simple keytool tasks** group will be added to the available tasks at your disposal. You can use the following command to see them:
>
> ```
> gradlew tasks --group "Simple Keytool"
> ```
>
> Expect to see the available tasks like the following:
>
> ```
> Simple Keytool tasks
> --------------------
> cacertsDelete - Convenience task to delete a certificate from cacerts.
> cacertsDeleteDir - Convenience task to delete certificates based on a directory from cacerts.
> cacertsImport - Convenience task to import a certificate to cacert.
> cacertsImportDir - Convenience task to import certificates from a directory to cacerts.
> cacertsList - Convenience task to display cacerts content.
> cacertsListDir - Convenience task to display cacerts content based on a directory.
> keytoolTask - Executes any valid java keytool command.
> ksDelete - Convenience task to delete a certificate from a keystore.
> ksDeleteDir - Convenience task to delete certificates based on a directory from a keystore.
> ksImport - Convenience task to import a certificate to a keystore.
> ksImportDir - Convenience task to import certificates from a directory to a keystore.
> ksList - Convenience task to display the keystore content.
> ksListDir - Convenience task to display the keystore content based on a directory.
> ```

## Plugin Properties

| Property | Description | Type | Default |
|-----|------|------|-----|
| simple_keytool.dirAliasPrefix | Holds the **desired prefix of the alias** of the convenience tasks that process directory. The alias parameter can be overridden by the file specific arguments. | String |  |
| simple_keytool.dirAliasSuffix | Holds the **desired suffix of the alias** of the convenience tasks that process directory. The alias parameter can be overridden by the file specific arguments. | String | [sk] |
| simple_keytool.javaHome | Holds the **location of the java JDK** to use by all the of the convenience tasks. | File |  |
| simple_keytool.noop | Indicates to **not actually execute the command** that it suppose to do but just display it, when set to true. | Boolean | false |
| simple_keytool.showExecCode | Indicates to **display the actual command** that was successfully executed, when set to true. | Boolean | false |
| simple_keytool.storePass | Holds the **default password** to use to by all the convenience tasks. | String | changeit |

## General Syntax

```
<KEYTOOL_EXECUTABLE> <KEYTOOL_COMMAND> <KEYTOOL_COMMAND_ARGS> <KEYTOOL_COMMAND_ZARGS>
```

| Token | Description | Task Property | Type |
|------|------|------|------|
| KEYTOOL_EXECUTABLE | The keytool executable. |  | |
| KEYTOOL_COMMAND | The keytool command to be executed *(e.g. -list, -importcert, et all)*. | command | String |
| KEYTOOL_COMMAND_ARGS | The arguments for the keytool command. | args | String[] |
| KEYTOOL_COMMAND_ZARGS | The arguments that will always be after the KEYTOOL_COMMAND_ARGS. This is optional. | zargs | String[] |

> All these task properties *(i.e. command, args, zargs)* are always available to all the tasks *(i.e. including the convenience tasks)*.

#### Example

```
keytool.exe -list -cacerts -storepass changeit
```

| Token                | Value                        |
| -------------------- | ---------------------------- |
| KEYTOOL_EXECUTABLE   | keytool.exe                  |
| KEYTOOL_COMMAND      | -list                        |
| KEYTOOL_COMMAND_ARGS | -cacerts -storepass changeit |

## CACerts vs Keystore Prefixed Tasks

For the **java version that supports -cacerts argument** use **cacerts prefixed tasks**. This parameter will not require you to specify the actual location of the cacerts file. Otherwise use the **ks prefixed tasks** and provide the **keyStore location**.    

## Usage

All the member tasks of **Simple Keytool** group is a child for **keytoolTask**. The **child task** normally just have a default command and/or arguments *(e.g. **cacertsList** task has **-list as the command and -cacerts as the arguments**)*. 

Whatever you can do with the **keytool command** in console you can do it in gradle with this task. 

| Task Name   | Task Property | Type     | Description                                                |
| ----------- | ------------- | -------- | ---------------------------------------------------------- |
| keytoolTask | args          | String[] | The arguments associated with the command.                 |
|             | command       | String   | The command to execute.                                    |
|             | isAdminMode   | boolean  | To run the command in elevated mode. The default is false. |
|             | javaHome      | File     | Target a specific JDK.                                     |
|             | zargs         | String[] | The arguments after all the args.                          |

#### Sample translation of keytool command to keytoolTask

Translate the following **keytool list command** into a task in gradle:

```
keytool.exe -list -cacerts -storepass changeit
```

**Using the keytoolTask would be like the following:**

```groovy
keytoolTask {
    command = '-list'
    args = ['-cacerts', '-storepass', 'changeit', '-v']
}
```

**Using the child task cacertsList would be the following:**

```groovy
cacertsList {
    args = ['-v']
}
```

> You don't need to set the **command property** because it was already preset with **-list** and the **-cacerts** as the default argument *(i.e. this is true to all the **cacerts prefixed** convenience tasks.)*. 

> All the convenience tasks will add the **-storepass arguments with password**. The **default password** can be changed by **simple_keytool.storePass property**. Moreover, you can always use the **storePass argument** of the child task. 

#### **Sample creation of your own task of type KeytoolTask**

```groovy
task ktCacertVerboseList(type: KeytoolTask) {
    command = '-list'
    args = ['-cacerts', '-storepass', 'changeit', '-v']
}
```

> To use **KeytoolTask class** as the type of your task, you must add the following at the top of your **build.gradle** file:
>
> ```groovy
> import xyz.ronella.gradle.plugin.simple.keytool.task.*
> ```
>
> Note: Each **convenience task** has equivalent class file. The class file has the prefix **CACerts** and **KS** that corresponds to **cacerts** and **ks** prefixed tasks *(e.g. **cacertsList** gradle task has an equivalent class of **CACertsListTask** and **ksList** has an equivalent class of **KSListTask**)*. Notice, that all the class equivalent has the suffix **Task**.

#### **Sample creation of your own task of type CACertsListTask for convenience**

``` groovy
task cacertRFCList(type: CACertsListTask) {
    args = ['-rfc']
}
```

> You don't need to set the **command property** because it was already preset with **-list** and the **-cacerts and -storepass arguments** were already added. The **default password for storepass** is defined by **simple_keytool.storePass**.

#### Sample usage of cacertsImportDir to import all the certificates from a directory

If the certificates are in the following directory:

```
C:\Secured\Certs
```

And it contains the following certificate files:

```
cert1.cer
cert2.cer
```

You can use cacertsImportDir to process the certs using the following and configure a **different alias for cert1.cer file**:

```groovy
cacertsImportDir {
    dir=file('C:\\Secured\\Certs')
    fileArgs = [
        'cert1.cer': ['-alias', 'cert1']
    ]
}
```

> The alias of cert2.cer file will be in the following format:
>
> **{simple_keytool.dirAliasPrefix>}<CERT_FILENAME>{<simple_keytool.dirAliasSuffix>}**
>
> Thus, it will be:
>
> **cert1.cer [sk]**
>
> The **[sk] suffix** is the default value of **simple_keytool.dirAliasSuffix** property. 

## Sample build.gradle File

``` groovy
plugins {
  id "xyz.ronella.simple-keytool" version "1.0.0"
}

task cacertRFCList(type: CACertsListTask) {
    args = ['-rfc']
}

cacertsImportDir {
    dir=file('C:\\Secured\\Certs')
    fileArgs = [
        'cert1.cer': ['-alias', 'cert1']
    ]
}
```
## Convenience Tasks and Their Task Properties

| Task Name       | Task Property | Task Type | Description |
| --------------- | ------------- | ------- | ------- |
| cacertsDelete <br />*(admin mode)* | alias<br />*(required)* |String  |The alias to be deleted.  |
| | storePass |String | The store password. <br />Uses the **simple_keytool.storePassword** by default. |
| | storeType |String | The store type. |
| | verbose |Boolean | Makes the output verbose. |
| cacertsDeleteDir <br />*(admin mode)* | dir<br />*(required)* | File                      | The directory that contains the certificates. |
|  | fileArgs | Map<String, List<String>> | Specifying arguments certificate filename. <br />The **key must be just the filename only** and **value must be a valid list of arguments**. |
| | storePass |String | The store password. <br />Uses the **simple_keytool.storePassword** by default. |
| | storeType |String | The store type. |
| | verbose |Boolean | Makes the output verbose. |
| cacertsImport <br />*(admin mode)* | alias<br />*(required)* |String  | The alias of the certificate to import.                      |
|  | file<br />*(required)* |File |The certificate file to import. |
|  | keyPass |String |The key password. |
|  | storePass |String |The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|  | storeType |String |The store type. |
|  | verbose |Boolean |Makes the output verbose. |
| cacertsImportDir<br />*(admin mode)* | dir<br />*(required)* |File|The directory that contains the certificates.|
|  | fileArgs |Map<String, List<String>>|Specifying arguments certificate filename. <br />The **key must be just the filename only** and **value must be a valid list of arguments**.|
|  | storePass |String|The store password. <br />Uses the **simple_keytool.storePassword** by default.|
|  | storeType |String|The store type.|
|  | verbose |Boolean|Makes the output verbose.|
| cacertsList | alias |String  |The alias to list if provided.  |
|  | storePass |String |The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|  | storeType |String |The store type. |
|  | verbose |Boolean |Makes the output verbose. |
| cacertsListDir | dir<br />(required) |File |The directory to base the output of the list. |
|  | fileArgs |Map<String, List<String>> |Specifying arguments certificate filename. <br />The **key must be just the filename only** and **value must be a valid list of arguments**. |
|  | storePass |String |The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|  | storeType |String |The store type. |
|  | verbose |Boolean |Makes the output verbose. |
| ksDelete<br />*(admin mode)* | alias<br />*(required)*    | String                    | The alias to be deleted.                                     |
|  | keyStore<br />*(required)* | File                      | The target keystore to import the certificate.               |
|  | storePass |String |The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|  | storeType |String |The store type. |
|  | verbose |Boolean |Makes the output verbose. |
| ksDeleteDir<br />*(admin mode)* | dir<br />*(required)*      | File                      | The directory that contains the certificates.                |
|  | fileArgs                   | Map<String, List<String>> | Specifying arguments certificate filename. <br />The **key must be just the filename only** and **value must be a valid list of arguments**. |
|  | keyStore<br />*(required)* |File |The target keystore to import the certificate. |
|  | storePass |String |The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|  | storeType |String |The store type. |
|  | verbose |Boolean |Makes the output verbose. |
| ksImport<br />*(admin mode)* | alias<br />*(required)*    | String                    | The alias of the certificate to import.                      |
|  | file<br />*(required)*     | File                      | The certificate file to import.                              |
|  | keyPass                    | String                    | The key password.                                            |
|  | keyStore<br />*(required)* | File                      | The target keystore to import the certificate.               |
|  | storePass                  | String                    | The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|  | storeType                  | String                    | The store type.                                              |
|  | verbose                    | Boolean                   | Makes the output verbose.                                    |
| ksImportDir<br />*(admin mode)* | dir<br />*(required)*      | File                      | The directory that contains the certificates.                |
|  | fileArgs                   | Map<String, List<String>> | Specifying arguments certificate filename. <br />The **key must be just the filename only** and **value must be a valid list of arguments**. |
|                                       | keyStore<br />*(required)* | File                      | The target keystore to import the certificate. |
|                                       | storePass                  | String                    | The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|                                       | storeType                  | String                    | The store type. |
|                                       | verbose                    | Boolean                   | Makes the output verbose. |
| ksList                                | alias<br />*(required)*    | String                    | The alias to list if provided. |
|                                       | keyStore<br />*(required)* | File                      | The target keystore |
|                                       | storePass                  | String                    | The store password. <br />Uses the **simple_keytool.storePassword** by default. |
|                                       | storeType                  | String                    | The store type. |
|                                       | verbose                    | Boolean                   | Makes the output verbose. |
| ksListDir | dir<br />*(required)* | File                      | The directory that contains the certificates.                |
|  | fileArgs | Map<String, List<String>> | Specifying arguments certificate filename. <br />The **key must be just the filename only** and **value must be a valid list of arguments**. |
|  | keyStore<br />*(required)* | File                      | The target keystore                                          |
| | storePass                  | String                    | The store password. <br />Uses the **simple_keytool.storePassword** by default. |
| | storeType                  | String                    | The store type.                                              |
|                                       | verbose                    | Boolean                   | Makes the output verbose. |

> All tasks in **admin mode** will be run in **elevated mode** to do its job. Expect to see windows dialog to allow it to make changes.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## [Build](BUILD.md)

## [Changelog](CHANGELOG.md)

## Author

* Ronaldo Webb