# Run Monolith (app-runner)

**Important:** You must run Maven **from the project root** (the directory that contains `auth-service/`, `puja-service/`, `app-runner/`, and the parent `pom.xml`).  
`app-runner` depends on `auth-service`, `puja-service`, `pandit-service`, and `order-service`; those must be **built and installed** to your local repo first.

## One-time build (from project root)

```bash
cd inxinfo-auth-service
mvn clean install
```

Use `-DskipTests` if you want to skip tests:

```bash
mvn clean install -DskipTests
```

## Run monolith

```bash
mvn spring-boot:run -pl app-runner
```

## One command (build dependencies + run)

This builds the services app-runner depends on, then runs app-runner:

```bash
mvn clean install -pl app-runner -am -DskipTests
mvn spring-boot:run -pl app-runner
```

`-am` = "also make" the modules that `app-runner` depends on (auth-service, puja-service, pandit-service, order-service).

## If you see "Could not find artifact com.satishlabs:auth-service"

You ran `spring-boot:run -pl app-runner` without building the rest of the project. Fix:

1. Open a terminal in the **project root** (not inside `app-runner`).
2. Run: `mvn clean install -DskipTests`
3. Then run: `mvn spring-boot:run -pl app-runner`

## From IntelliJ IDEA

1. **Build:** Run Maven goal `install` for the **root** project (right-click root `pom.xml` → Maven → Run Maven Goal → `install`), or run in terminal from root: `mvn clean install -DskipTests`.
2. **Run:** Run the main class `com.satishlabs.InxinfoApplication` from the `app-runner` module, or use Maven goal `spring-boot:run` with profile `app-runner` **after** the root project has been built.

If your IDE runs Maven with `-pl app-runner` only, it will not build the other modules. Either run a full `install` from the root first, or configure the Run Configuration to use "Run Maven Goal" on the **root** with `clean install -pl app-runner -am` then `spring-boot:run -pl app-runner`.
