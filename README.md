# JSONBin.io Java SDK

[![codecov](https://codecov.io/gh/OdunlamiZO/java-jsonbin/graph/badge.svg?token=HULR9R4NAH)](https://codecov.io/gh/OdunlamiZO/java-jsonbin)

A minimal and type-safe Java SDK for interacting with JsonBin.io. This SDK allows developers to read and deserialize JSON documents from public or private bins using a clean, strongly-typed API.

## Requirements

- Java 17 or higher
- Maven (for dependency management)

## Installation

1. Add the GitHub Maven repository to your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub Packages - java-jsonbin</name>
    <url>https://maven.pkg.github.com/OdunlamiZO/java-jsonbin</url>
  </repository>
</repositories>
```

2. Add the dependency:

```xml
<dependency>
  <groupId>io.github.odunlamizo</groupId>
  <artifactId>java-jsonbin</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

3. Configure GitHub credentials in your Maven settings.xml (usually located at ~/.m2/settings.xml):

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

> 📌&nbsp;&nbsp;&nbsp; Step 1 & 3 is required for now, since we are only deploying to github packages.

## Getting Started

### Setup

```java
JsonBin<UserList> jsonBin =
        new JsonBinOkHttp.Builder().withMasterKey("JSONBIN_MASTER_KEY").build(UserList.class);
```

### Example: Calling the API

Here's a basic example of using the SDK to read a bin from JSONBin.io:

```java
Bin<UserList> bin = jsonBin.readBin("687644d36063391d31ae163f");
System.out.println(bin);
// Bin(record={users=[{name=Morounfoluwa Mary, age=19}]}, metadata=Metadata(id=687644d36063391d31ae163f, _private=false, createdAt=2025-07-15T12:08:51.887Z, name=Java SDK Test))
```

## Contributing

We welcome contributions to improve this SDK! To contribute:

1. **Fork** the repository.
2. **Create a new branch** for your feature or bugfix.
3. Make your changes and write appropriate tests.
4. **Open a Pull Request (PR)** to the `main` branch with a clear description of your changes.

> 📌&nbsp;&nbsp;&nbsp;Please ensure your code adheres to the project's style and passes all tests before submitting a PR.