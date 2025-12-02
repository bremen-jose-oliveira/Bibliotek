# Fix Apple OAuth Login

## Current Status
✅ Google OAuth is working
❌ Apple OAuth is not working

## Common Apple OAuth Issues

### Issue 1: Key ID Mismatch

Your `.env` file shows `APPLE_KEY_ID=3NJ23GPMBU` but `application.yml` shows `key-id: YV7L248U6W`.

**Check which one is correct:**
1. Go to: https://developer.apple.com/account/resources/authkeys/list
2. Check which Key ID you're actually using
3. Make sure it matches in:
   - Fly.io secrets
   - application.yml
   - The private key filename

### Issue 2: Private Key File Missing

The code looks for: `src/main/resources/AuthKey_3NJ23GPMBU.p8`

**Check:**
1. Does this file exist in your project?
2. Is it included in the Docker image?
3. Does the filename match your Key ID?

### Issue 3: Wrong Secrets in Fly.io

**Verify secrets are set correctly:**
```powershell
fly secrets list -a jose-long-morning-2431
# Should show:
# APPLE_TEAM_ID
# APPLE_KEY_ID
```

**Set them if missing or wrong:**
```powershell
# If using YV7L248U6W (from application.yml):
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5 -a jose-long-morning-2431
fly secrets set APPLE_KEY_ID=YV7L248U6W -a jose-long-morning-2431

# OR if using 3NJ23GPMBU (from .env):
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5 -a jose-long-morning-2431
fly secrets set APPLE_KEY_ID=3NJ23GPMBU -a jose-long-morning-2431
```

### Issue 4: Redirect URI Not Authorized

**Check Apple Developer Console:**
1. Go to: https://developer.apple.com/account/resources/identifiers/list/serviceId
2. Find your Service ID: `com.jose-oliv.mypersonallibraryfront.si`
3. Click to edit
4. Under "Return URLs", make sure you have:
   ```
   https://jose-long-morning-2431.fly.dev/login/oauth2/code/apple
   ```

### Issue 5: Client ID Mismatch

**Verify in application.yml:**
```yaml
apple:
  client-id: com.jose-oliv.mypersonallibraryfront.si
```

**Must match Apple Developer Console Service ID exactly.**

## Step-by-Step Fix

### Step 1: Check Current Secrets

```powershell
fly secrets list -a jose-long-morning-2431
```

### Step 2: Decide Which Key ID to Use

**Option A: Use YV7L248U6W (from application.yml)**
- Update `.env` to match
- Set Fly.io secrets to YV7L248U6W
- Make sure private key file is: `AuthKey_YV7L248U6W.p8`

**Option B: Use 3NJ23GPMBU (from .env)**
- Update `application.yml` line 64: `key-id: 3NJ23GPMBU`
- Update `AppleClientSecretProvider.java` line 33: `AuthKey_3NJ23GPMBU.p8`
- Set Fly.io secrets to 3NJ23GPMBU
- Make sure private key file is: `AuthKey_3NJ23GPMBU.p8`

### Step 3: Set Correct Secrets in Fly.io

```powershell
# Example using 3NJ23GPMBU:
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5 -a jose-long-morning-2431
fly secrets set APPLE_KEY_ID=3NJ23GPMBU -a jose-long-morning-2431
```

### Step 4: Verify Private Key File

1. Check if the `.p8` file exists in `src/main/resources/`
2. Make sure it's not in `.gitignore` (it should be - don't commit private keys!)
3. Make sure it's included in the Docker build

**Important:** Private keys should NOT be committed to Git. They should be:
- Added to Docker image during build
- Or stored as a Fly.io secret and loaded at runtime

### Step 5: Check Apple Developer Console

1. **Service ID:** https://developer.apple.com/account/resources/identifiers/list/serviceId
   - Verify: `com.jose-oliv.mypersonallibraryfront.si` exists
   - Check Return URLs include: `https://jose-long-morning-2431.fly.dev/login/oauth2/code/apple`

2. **Keys:** https://developer.apple.com/account/resources/authkeys/list
   - Verify your Key ID exists
   - Download the key if needed (you can only download once!)

### Step 6: Restart and Test

```powershell
fly restart -a jose-long-morning-2431
fly logs -a jose-long-morning-2431
# Look for Apple OAuth errors
```

## Check Logs for Specific Errors

```powershell
fly logs -a jose-long-morning-2431 | Select-String -Pattern "Apple|apple|OAuth|oauth"
```

Common error messages:
- `invalid_client` - Wrong client ID, team ID, or key ID
- `invalid_grant` - Wrong redirect URI or expired token
- `FileNotFoundException` - Private key file missing
- `Could not resolve placeholder` - Missing environment variable

## Quick Fix (Most Likely Issue)

Based on your setup, the most likely issue is the Key ID mismatch:

```powershell
# Use the Key ID from your .env (3NJ23GPMBU)
fly secrets set APPLE_KEY_ID=3NJ23GPMBU -a jose-long-morning-2431

# Update application.yml to match
# Change line 64 from: key-id: YV7L248U6W
# To: key-id: 3NJ23GPMBU

# Update AppleClientSecretProvider.java line 33
# Change from: AuthKey_3NJ23GPMBU.p8 (if it's different)

# Commit and redeploy
git add .
git commit -m "Fix Apple OAuth Key ID to match .env"
git push origin master
fly deploy -a jose-long-morning-2431
```

## Need Help?

Run this and share the output:
```powershell
fly logs -a jose-long-morning-2431 | Select-String -Pattern "Apple" -Context 5
```

This will show the exact error message.

