const express = require("express");
const app = express();

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

/*
 SQL Injection Vulnerability
*/
app.get("/user", (req, res) => {
  const userId = req.query.id;

  // ❌ Vulnerable query construction
  const query = "SELECT * FROM users WHERE id = " + userId;

  res.send("Executing query: " + query);
});

/*
 XSS Vulnerability
*/
app.get("/search", (req, res) => {
  const term = req.query.term;

  // ❌ Unsanitized user input rendered directly
  res.send(`<h1>Search Results for: ${term}</h1>`);
});

app.listen(3000, () => {
  console.log("App running on port 3000");
});
app.get("/login", (req, res) => {
  const username = req.query.username;
  const password = req.query.password;

  const query =
    "SELECT * FROM users WHERE username='" +
    username +
    "' AND password='" +
    password +
    "'";

  res.send(query);
});
app.get("/config", (req, res) => {
  const dbPassword = "SuperSecretPassword123!";
  const apiKey = "sk_live_123456789abcdef";

  res.json({
    database: "prod-db",
    username: "admin",
    password: dbPassword,
    stripeKey: apiKey
  });
});