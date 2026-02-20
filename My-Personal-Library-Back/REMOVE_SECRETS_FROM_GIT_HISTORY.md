# Remove Secrets from Git History

**Problem:** `application.properties` (and possibly other files) were committed in the past with **passwords and API keys** in plaintext. Those values remain in the repository history and can be exposed to anyone with clone access.

**Goals:**
1. Replace the tracked `application.properties` with a safe version that uses **only environment variables** (no secrets). ✅ Done in the repo.
2. **Rewrite Git history** so that every past commit that contained secrets no longer contains them (or contains the safe template instead).
3. Update the remote (`origin`) with the rewritten history.

---

## Step 1: Use the Safe Config (Already Done)

The file `My-Personal-Library-Back/src/main/resources/application.properties` has been updated to use only placeholders like `${DB_PW}`, `${GOOGLE_CLIENT_SECRET}`, `${APPLE_SECRET}`, etc. **Set these in your environment** (e.g. Fly.io secrets, or a local `.env` that is **not** committed).

---

## Step 2: Remove Secrets from All Commits (Rewrite History)

You have two main options.

### Option A: Replace the file in every commit (recommended)

Use **git-filter-repo** (faster and safer than `filter-branch`).

1. **Install git-filter-repo** (if needed):
   ```bash
   pip install git-filter-repo
   # or: brew install git-filter-repo
   ```

2. **Clone a fresh copy** of the repo (to avoid damaging your current clone):
   ```bash
   cd /tmp
   git clone --no-hardlinks <your-repo-url> bibliotek-clean
   cd bibliotek-clean
   ```

3. **Create a script** that rewrites `application.properties` to the safe content in every commit.  
   For example, save the **current safe** `application.properties` content to a file `safe-application.properties`, then run:
   ```bash
   git filter-repo --replace-text <(echo "===REPLACE_ME===
   $(cat My-Personal-Library-Back/src/main/resources/application.properties)
   ===END===") --path My-Personal-Library-Back/src/main/resources/application.properties --force
   ```
   Actually, `--replace-text` replaces text patterns, not whole file. To **replace the entire file** in every commit, use a **tree filter** or **blob filter**. With git-filter-repo, the cleanest is:

   **Alternative: use BFG to replace a specific file in history**

   - Copy your **safe** `application.properties` to a file e.g. `safe-app.properties`.
   - BFG doesn’t support “replace file X with file Y in all commits” directly. So the usual approach is either:
     - **git filter-repo** with a custom “filename filter” that, for that path, substitutes the blob with the safe content, or  
     - **git filter-branch** (see Option B).

4. **Re-add the remote** (git-filter-repo removes remotes):
   ```bash
   git remote add origin <your-repo-url>
   ```

5. **Force-push** (this rewrites remote history; coordinate with anyone else using the repo):
   ```bash
   git push --force origin master
   ```

### Option B: git filter-branch (replace file in all commits)

From the **root of the Bibliotek repo**:

```bash
# Backup branch first
git branch backup-master-before-history-rewrite

# Create the safe file path (current safe content)
SAFE_FILE="My-Personal-Library-Back/src/main/resources/application.properties"

# Rewrite every commit: replace that file with the current (safe) version
git filter-branch -f --tree-filter "
  if [ -f \"$SAFE_FILE\" ]; then
    cp \"$SAFE_FILE\" \"$SAFE_FILE\".safe;
  fi
" --prune-empty master

# Then in each commit, overwrite the file with the safe content (simplified:
# you'd copy from a stored "safe" file). See below for a full tree-filter.
```

A **full tree-filter** that actually replaces the file content in every commit would look like this (run from repo root, with the safe content already in a file named `safe-application.properties` in the repo root):

```bash
git filter-branch -f --tree-filter '
  F=My-Personal-Library-Back/src/main/resources/application.properties
  if [ -f "$F" ]; then
    cp safe-application.properties "$F"
  fi
' --prune-empty -- --all
```

You’d need to put the safe content into `safe-application.properties` once before running this.

---

## Step 3: Rotate All Exposed Secrets

Even after rewriting history:

- Assume every **password and API key** that ever appeared in a commit is **compromised**.
- **Rotate** them: new DB password, new SMTP password, new Google OAuth client secret, new Apple secret/key, etc.
- Update Fly.io (or your host) and local `.env` with the new values only.

---

## Step 4: Push and Notify

- After rewriting history: `git push --force origin master` (from the clone you used for the rewrite).
- **Warn** anyone who has cloned the repo that history was rewritten; they should re-clone or run:
  ```bash
  git fetch origin
  git reset --hard origin/master
  ```

---

## Summary Checklist

- [ ] Safe `application.properties` committed (no plaintext secrets). ✅ Already in this branch.
- [ ] Install git-filter-repo or use filter-branch; run history rewrite in a clone.
- [ ] Force-push rewritten `master` to `origin`.
- [ ] Rotate every secret that ever appeared in the old history.
- [ ] Set all env vars (Fly.io secrets / .env) with the new values.
