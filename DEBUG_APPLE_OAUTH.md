# Debug Apple OAuth - Step by Step

## Current Issue
After clicking Apple login, you get `[invalid_client]` error and end up back at the login page.

## Step 1: Check Fly.io Logs for Exact Error

```powershell
fly logs -a jose-long-morning-2431 | Select-String -Pattern "Apple|apple|OAuth|invalid" -Context 3
```

This will show the exact error message.

## Step 2: Verify All Secrets Are Set

```powershell
fly secrets list -a jose-long-morning-2431
```

**Must have:**
- `APPLE_TEAM_ID=YJYA3WJYT5`
- `APPLE_KEY_ID=3NJ23GPMBU`
- `BACK_END_URL=https://jose-long-morning-2431.fly.dev` (must be HTTPS!)

## Step 3: Check Private Key File

The code looks for: `src/main/resources/AuthKey_3NJ23GPMBU.p8`

**Verify:**
1. Does this file exist in your project?
2. Is it included in the Docker image? (Check if it's in `.gitignore` - it shouldn't be committed, but needs to be in Docker)

## Step 4: Verify Apple Developer Console

1. **Service ID:** https://developer.apple.com/account/resources/identifiers/list/serviceId
   - Service ID must be: `com.jose-oliv.mypersonallibraryfront.si`
   - Return URLs must include: `https://jose-long-morning-2431.fly.dev/login/oauth2/code/apple`

2. **Keys:** https://developer.apple.com/account/resources/authkeys/list
   - Key ID must be: `3NJ23GPMBU`
   - Team ID must be: `YJYA3WJYT5`

## Step 5: Common Issues and Fixes

### Issue: Private Key File Not in Docker Image

**Check if file exists:**
```powershell
# In your project directory
Test-Path "src/main/resources/AuthKey_3NJ23GPMBU.p8"
```

**If missing:** You need to add it to the Docker image. Private keys should NOT be in Git, but need to be in the Docker build.

**Solution:** Add the file to Docker during build, or store it as a Fly.io secret and load it at runtime.

### Issue: Wrong Redirect URI

**Check Apple Developer Console:**
- Return URL must be EXACTLY: `https://jose-long-morning-2431.fly.dev/login/oauth2/code/apple`
- Must be HTTPS (not HTTP)
- No trailing slash

### Issue: Client ID Mismatch

**Verify in application.yml:**
```yaml
client-id: com.jose-oliv.mypersonallibraryfront.si
```

**Must match Apple Developer Console Service ID exactly.**

### Issue: Key ID Mismatch

**Verify:**
- Fly.io secret: `APPLE_KEY_ID=3NJ23GPMBU`
- application.yml: `key-id: 3NJ23GPMBU`
- Private key filename: `AuthKey_3NJ23GPMBU.p8`
- Apple Developer Console Key ID: `3NJ23GPMBU`

All must match!

## Quick Diagnostic Commands

```powershell
# 1. Check secrets
fly secrets list -a jose-long-morning-2431

# 2. Check recent logs
fly logs -a jose-long-morning-2431 --limit 50

# 3. Search for Apple errors
fly logs -a jose-long-morning-2431 | Select-String -Pattern "Apple|Failed|Exception" -Context 5
```

## Most Likely Issues

1. **Private key file not in Docker image** - Check if `AuthKey_3NJ23GPMBU.p8` is included
2. **Redirect URI not authorized** - Check Apple Developer Console
3. **Secrets not set correctly** - Verify `APPLE_KEY_ID` and `APPLE_TEAM_ID` in Fly.io

## Next Steps

1. Run the log command above to see the exact error
2. Share the error message so we can fix it precisely
3. Verify the private key file exists and is in the Docker image

