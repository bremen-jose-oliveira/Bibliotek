# Backend: dev vs master – Comparison & Notifications

## Summary

| Branch   | More updated?     | Notifications                          | Backend location in repo      |
|----------|-------------------|----------------------------------------|-------------------------------|
| **master** | ✅ **Yes** (27 commits ahead) | ❌ **No** – no Notification entity/table | `My-Personal-Library-Back/`   |
| **dev**    | ❌ Behind master   | ✅ **Yes** – full notification system   | Repo root (no subfolder)      |

**Conclusion:** **master** is the more updated branch (all of dev + 27 commits). **Notifications exist only on dev**; they were dropped (or not carried over) when the backend was moved under `My-Personal-Library-Back/` on master.

---

## What happened to notifications

1. **Commit `91a845e7` (on dev):**  
   *"feat: implement Apple OAuth2 client secret generation and add notification system with controllers, services, and DTOs"*  
   - Added full notification support: `Notification` entity (table `notifications`), `NotificationController`, `NotificationService`, `NotificationRepository`, `NotificationDTO`, `NotificationMapper`.

2. **master branched from that commit** and then had **27 more commits**, including:
   - `a3f76011` – Refactor Notification classes (formatting)
   - `76fb590e` – User/Book/Friendship refactors
   - `8477c6bd` – Add initial project structure (backend under **My-Personal-Library-Back/**)
   - Plus: borrowed/lending endpoints, user book status, description field, OAuth improvements, etc.

3. **On current master:**  
   Backend code lives under **My-Personal-Library-Back/** and **does not include** any Notification classes. So either:
   - The move to `My-Personal-Library-Back/` was done from a tree that didn’t have notifications, or  
   - Notifications were removed or not re-added after the restructure.

So: **notifications “disappeared” on master** because the branch evolved (and/or was reorganized) without keeping the Notification feature from dev.

---

## Files present on dev, missing on master (notification-related)

| On dev | On master |
|--------|-----------|
| `controller/NotificationController.java` | ❌ |
| `dto/NotificationDTO.java` | ❌ |
| `entity/Notification.java` (table `notifications`) | ❌ |
| `mapper/NotificationMapper.java` | ❌ |
| `repository/NotificationRepository.java` | ❌ |
| `service/NotificationService.java` | ❌ |

---

## Other differences

- **dev** has backend at **repo root** (paths like `src/main/java/...`, `controller/NotificationController.java`).
- **master** has backend under **My-Personal-Library-Back/** (paths like `My-Personal-Library-Back/src/main/java/...`).
- **master** has many features dev doesn’t: borrowed/lending endpoints, user book status API, book description, OAuth improvements, account deletion, etc.

---

## How to get notifications back on master

1. **Option A – Restore from dev (recommended)**  
   From the **dev** branch, copy these files into **My-Personal-Library-Back/** with the same package/path structure as on master:
   - `entity/Notification.java`
   - `repository/NotificationRepository.java`
   - `service/NotificationService.java`
   - `controller/NotificationController.java`
   - `dto/NotificationDTO.java`
   - `mapper/NotificationMapper.java`  
   Then fix package names if needed (dev may use `com.bibliotek.personal`, master uses `com.Bibliotek.personal`), add the `notifications` table (e.g. via migration or JPA schema update), and ensure any dependencies (e.g. `ReviewController`, `ReviewService`) exist on master.

2. **Option B – Merge dev into master**  
   Merge **dev** into **master**. You’ll get notification code back but also have to resolve the structural difference (backend at root vs under **My-Personal-Library-Back/**) and possibly duplicate or conflicting files. Usually Option A is simpler.

3. **Option C – Re-implement**  
   Re-add the notification entity, table, repository, service, controller, DTO, and mapper on master from scratch, using dev’s implementation as reference.

---

---

## Will the notification system be in sync with the frontend?

**Yes.** The frontend is already built to work with the API that the **dev** notification system exposes.

### Frontend expects (NotificationContext + Interfaces/notification.ts)

| Frontend expects | Backend on dev | Match |
|------------------|----------------|------|
| GET `/api/notifications` | ✅ Same | ✅ |
| GET `/api/notifications/unread/count` → `{ count: number }` | ✅ `Map.of("count", count)` | ✅ |
| PUT `/api/notifications/{id}/read` | ✅ Same | ✅ |
| PUT `/api/notifications/read-all` | ✅ Same | ✅ |
| DELETE `/api/notifications/{id}` | ✅ Same | ✅ |
| DELETE `/api/notifications` | ✅ Same | ✅ |

### Response shape

- Frontend expects each notification: `id` (string), `type`, `title`, `message`, `read`, `createdAt`, `updatedAt`, `relatedId?`, `relatedEmail?`, `relatedBookId?`.
- Backend `NotificationDTO` has the same fields (frontend converts `id` to string via `n.id.toString()`).
- `NotificationContext` already maps the backend response to the frontend `Notification` type.

### Notification types

- Frontend enum: `FRIEND_REQUEST`, `FRIEND_REQUEST_ACCEPTED`, `EXCHANGE_REQUEST`, `EXCHANGE_ACCEPTED`, `EXCHANGE_REJECTED`, `EXCHANGE_RETURNED`, `REVIEW_ADDED`.
- Backend entity enum on dev matches these.

So if you restore the notification system from **dev** into **master** (same endpoints and DTO/entity), **no frontend changes are needed** – it will be in sync. Only ensure the restored backend uses the same package structure as master (`com.Bibliotek.personal`) so it compiles and the `notifications` table is created (e.g. JPA or migration).

---

**Last checked:** Comparison of `origin/master` vs `origin/dev` for backend and Notification-related files.
