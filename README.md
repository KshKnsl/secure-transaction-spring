# SecurePay – Secure Transaction System

A full-stack money transfer platform built with **Spring Boot 4**. It implements a double-entry ledger model with idempotent transfers, JWT cookie-based authentication, and a clean React frontend.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 24, Spring Boot 4.0.3 |
| Database | MongoDB (Spring Data) |
| Auth | JWT (JJWT 0.12.6) + HttpOnly cookies |
| Email | Spring Mail (SMTP) |
| Frontend | Next.js 16, React 19, Tailwind CSS v4 |
| Build | Gradle (backend), npm (frontend) |


## Environment Variables

### Backend

| Variable | Description |
|---|---|
| `MONGO_URI` | MongoDB connection string |
| `JWT_SECRET` | Secret key for signing JWTs (min. 32 chars) |
| `EMAIL_USER` | SMTP username (Gmail) |
| `EMAIL_PASSWORD` | SMTP password / app password |

Set these in your environment or create a `.env` file before running.

## Running Locally

### Backend- The API starts on **http://localhost:8080**.

```bash
./gradlew bootRun
```

### Frontend

```bash
cd client
npm install
npm run dev
```

---

## Key Design Decisions

- **Double-entry ledger** — every transfer writes two `LedgerEntry` documents (DEBIT + CREDIT), making the balance a derived, auditable value.
- **Idempotency keys** — `Transaction` documents have a unique index on `idempotencyKey`, preventing duplicate transactions from retries.
- **Token blacklist** — logout inserts the JWT into a `TokenBlacklist` collection with a TTL index that auto-expires entries after 3 days (matching JWT expiry).
- **Stateless sessions** — no `HttpSession` is created; all auth state lives in the HttpOnly JWT cookie.
- **System users** — accounts flagged `systemUser = true` can call the `/system/initial-funds` endpoint to seed balances; all other callers receive `403 Forbidden`.

---

## Postman

Import the collection and environment from the `postman/` directory to explore all endpoints with pre-configured examples.

---

## License

[MIT](LICENSE)