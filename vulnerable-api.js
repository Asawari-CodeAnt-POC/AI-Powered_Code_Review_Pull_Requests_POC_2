// file: vulnerable-api.js

const express = require("express");
const fs = require("fs");
const { exec } = require("child_process");

const app = express();
app.use(express.json());

/*
 Command Injection Vulnerability
*/
app.get("/ping", (req, res) => {
  const host = req.query.host;

  // ❌ Command Injection
  exec("ping -c 1 " + host, (error, stdout, stderr) => {
    if (error) {
      return res.send(error.message);
    }
    res.send(stdout);
  });
});

/*
 Path Traversal Vulnerability
*/
app.get("/download", (req, res) => {
  const file = req.query.file;

  // ❌ Path Traversal
  fs.readFile("./uploads/" + file, "utf8", (err, data) => {
    if (err) {
      return res.send("File not found");
    }
    res.send(data);
  });
});

/*
 Open Redirect Vulnerability
*/
app.get("/redirect", (req, res) => {
  const url = req.query.url;

  // ❌ Open Redirect
  res.redirect(url);
});

/*
 Hardcoded JWT Secret
*/
const JWT_SECRET = "mySuperWeakHardcodedSecret";

/*
 Insecure CORS Configuration
*/
app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "*");
  next();
});

/*
 Sensitive Debug Info Exposure
*/
app.get("/debug", (req, res) => {
  res.json({
    env: process.env,
    jwtSecret: JWT_SECRET
  });
});

app.listen(4000, () => {
  console.log("Vulnerable API running on port 4000");
});