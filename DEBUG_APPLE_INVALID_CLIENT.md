# Debug Apple OAuth invalid_client Error

## Current Issue
Still getting `[invalid_client]` error when trying to log in with Apple.

## Step 1: Check Fly.io Logs for Exact Error

```powershell
fly logs -a jose-long-morning-2431 | Select-String -Pattern "Apple|apple|invalid|Failed|Exception" -Context 5
```

This will show the exact error message.

## Step 2: Verify All Secrets Are Set Correctly

```powershell
fly secrets list -a jose-long-morning-2431
```

**Must have:**
- `APPLE_TEAM_ID=YJYA3WJYT5`
- `APPLE_KEY_ID=3NJ23GPMBU`
- `BACK_END_URL=https://jose-long-morning-2431.fly.dev` (HTTPS!)

## Step 3: Verify Private Key File is in Docker Image

The private key file `AuthKey_3NJ23GPMBU.p8` must be included in the Docker image.

**Check if it's in the build:**
- The file should be at: `src/main/resources/AuthKey_3NJ23GPMBU.p8`
- Dockerfile copies `src/` which should include it
- But if it's in `.gitignore`, it won't be in GitHub, so Fly.io won't have it

## Step 4: Check Apple Developer Console

1. **Service ID:** https://developer.apple.com/account/resources/identifiers/list/serviceId
   - Service ID: `com.jose-oliv.mypersonallibraryfront.si`
   - Return URLs must include: `https://jose-long-morning-2431.fly.dev/login/oauth2/code/apple`

2. **Keys:** https://developer.apple.com/account/resources/authkeys/list
   - Key ID: `3NJ23GPMBU`
   - Team ID: `YJYA3WJYT5`

## Common Causes of invalid_client

### Cause 1: Private Key File Missing from Docker Image

**Problem:** The `.p8` file is not in the Docker image because it's in `.gitignore`.

**Solution:** 
- Option A: Store private key as Fly.io secret and load it at runtime
- Option B: Include the file in Docker build (but don't commit to Git)

### Cause 2: Wrong Client ID

**Check:** `application.yml` has:
```yaml
client-id: com.jose-oliv.mypersonallibraryfront.si
```

**Must match Apple Developer Console Service ID exactly.**

### Cause 3: Wrong Redirect URI

**Check:** Apple Developer Console Return URLs must have:
```
https://jose-long-morning-2431.fly.dev/login/oauth2/code/apple
```

**Must be HTTPS, exact match, no trailing slash.**

### Cause 4: Wrong Key ID or Team ID

**Verify:**
- Fly.io secret `APPLE_KEY_ID=3NJ23GPMBU`
- Fly.io secret `APPLE_TEAM_ID=YJYA3WJYT5`
- Must match Apple Developer Console

## Quick Diagnostic

Run these commands and share the output:

```powershell
# 1. Check secrets
fly secrets list -a jose-long-morning-2431

# 2. Check recent logs for Apple errors
fly logs -a jose-long-morning-2431 --limit 100 | Select-String -Pattern "Apple" -Context 3

# 3. Check if app is running
fly status -a jose-long-morning-2431
```

## Most Likely Issue: Private Key Not in Docker Image

If the private key file is in `.gitignore`, it won't be in GitHub, so Fly.io can't build it into the Docker image.

**Solution:** We need to either:
1. Store the private key as a Fly.io secret and load it at runtime, OR
2. Manually add it to the Docker build process

Let me know what the logs show and we can fix it!

