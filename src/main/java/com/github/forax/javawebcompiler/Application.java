package com.github.forax.javawebcompiler;

import module java.base;
import module java.compiler;

import tools.jackson.databind.ObjectMapper;

import javax.tools.ToolProvider;

public class Application {

  static void main(String[] args) {
    var app = JExpress.express();

    // Serve the static frontend files from "public"
    app.use(JExpress.staticFiles(Path.of("public")));

    var objectMapper = new ObjectMapper();

    app.post("/compile", (req, res) -> {
      try {
        var body = req.bodyText();
        var compileRequest = objectMapper.readValue(body, CompileRequest.class);
        var sourceCode = Compiler.compileRequest.code;
        var diagnostics = Compiler.compileInMemory("Main", sourceCode);
        res.send(objectMapper.writeValueAsString(diagnostics));
      } catch (Exception e) {
        res.status(500).json("""
            {"error": "Internal Server Error"}
            """);
      }
    });

    app.listen(8080);
    System.out.println("Web site on http://localhost:8080/index.html");
  }
}