# Spark Skeleton ⚡

Spark Skeleton brings modern development conventions (annotation-based routing, fluent migrations, dependency injection) to the Spark Java framework. No more manually declared routes and raw SQL migrations.
```java
@Controller
public class BlogController extends BaseController {
    
    @GET(value = "/blog", name = "blog_index")
    private Object index(ArticleRepository articleRepo) {
        List<Article> articles = DB.withConnection(() ->
            articleRepo.findPublished().stream().toList()
        );
        
        return render("blog/index.html", Map.of(
            "articles", articles
        ));
    }
}
```

## 🎯 Why this project?

Spark Java is an excellent micro-framework, but it lacks modern conventions. This boilerplate fills the gap by adding:

- **Routing annotations** so you no longer declare routes manually
- **A migration system** with a fluent API inspired by Laravel
- **The Repository pattern** with automatic dependency injection
- **A custom ErrorHandler** for clean stack traces in development
- **A template engine** (Pebble) integrated directly into controllers

## ✨ Main Features

| Feature | Description |
|---------|-------------|
| 🛣️ **Annotation-based routing** | `@GET`, `@POST`, `@PUT`, `@DELETE`, `@PATCH` on your methods |
| 🗃️ **Fluent migrations** | `table.string("title").notNull()` instead of raw SQL |
| 💉 **Dependency Injection** | Automatically injects your `@Repository` into controllers |
| 📦 **ActiveRecord models** | ActiveJDBC with getters/setters to manipulate your models cleanly |
| 🎨 **Integrated templating** | `render("view.html", data)` directly in your controllers |
| 🐛 **Custom Error Handler** | Detailed stack traces in dev, clean pages in production |

## 🚀 Quick Start
```bash
git clone https://github.com/kainovaii/spark-skeleton.git
cd spark-skeleton
./build.bat
```

→ The app runs on `http://localhost:8888`

## 📦 Tech Stack

- **Spark Java** - Web micro-framework
- **ActiveJDBC** - Lightweight ORM with ActiveRecord pattern
- **Pebble** - Modern template engine
- **Maven** - Build & dependency management

## 🔥 Quick Examples

### A controller with injection
```java
@Controller
public class ArticleController extends BaseController {
    
    @GET(value = "/articles/:id", name = "articles.show")
    private Object show(Request req, Response res, ArticleRepository repo) {
        String id = req.params(":id");
        Article article = DB.withConnection(() -> repo.findById(id));
        
        return render("articles/show.html", Map.of("article", article));
    }
}
```

### A fluent migration
```java
public class CreateArticlesTable extends Migration {
    @Override
    public void up() {
        createTable("articles", table -> {
            table.id();
            table.string("title").notNull();
            table.text("content");
            table.timestamps();
        });
    }
}
```

### A simple repository
```java
@Repository
public class ArticleRepository {
    public LazyList<Article> findPublished() {
        return Article.where("status = ?", 1);
    }
}
```

## 📖 Documentation

Full documentation is available at `http://localhost:8888/docs` once the app is running.

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you'd like to change.

## 📝 License
[MIT](LICENSE)