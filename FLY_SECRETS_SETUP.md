# Fly.io Secrets Setup Guide

## Current Issue
Backend is crashing because `APPLE_TEAM_ID` and `APPLE_KEY_ID` environment variables are missing.

## Quick Fix

### Set Required Secrets in Fly.io

Run these commands in your backend directory:

```bash
cd My-Personal-Library-Back

# Set Apple OAuth secrets (using values from application.yml)
# Use -a flag to specify app name
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5 -a jose-long-morning-2431
fly secrets set APPLE_KEY_ID=YV7L248U6W -a jose-long-morning-2431

# Verify secrets are set
fly secrets list -a jose-long-morning-2431
```

### All Required Secrets

Make sure these are all set:

```bash
# IMPORTANT: Use -a jose-long-morning-2431 for all commands

# Database
fly secrets set DB_URL=your_postgres_url -a jose-long-morning-2431
fly secrets set DB_USER=your_db_user -a jose-long-morning-2431
fly secrets set DB_PW=your_db_password -a jose-long-morning-2431

# Backend/Frontend URLs
fly secrets set BACK_END_URL=https://jose-long-morning-2431.fly.dev -a jose-long-morning-2431
fly secrets set FRONT_END_URL=https://p-lib.netlify.app -a jose-long-morning-2431

# JWT
fly secrets set JWT_SECRET_KEY=your_jwt_secret_key -a jose-long-morning-2431

# SMTP
fly secrets set SMTP_EMAIL=your_smtp_email -a jose-long-morning-2431

# Google OAuth
fly secrets set GOOGLE_CLIENT_SECRET=your_google_client_secret -a jose-long-morning-2431

# Apple OAuth (NEW - this was missing!)
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5 -a jose-long-morning-2431
fly secrets set APPLE_KEY_ID=YV7L248U6W -a jose-long-morning-2431
```

## After Setting Secrets

1. **Restart the app:**
   ```bash
   fly restart -a jose-long-morning-2431
   ```

2. **Check logs:**
   ```bash
   fly logs -a jose-long-morning-2431
   ```

3. **Verify it's running:**
   ```bash
   fly status -a jose-long-morning-2431
   ```

## Verify Secrets

```bash
# List all secrets
fly secrets list -a jose-long-morning-2431

# Should show all the variables above
```

## Code Fix Applied

I've also updated `AppleClientSecretProvider.java` to use default values from `application.yml` if environment variables aren't set. This provides a fallback, but it's still better to set them as secrets.

## Next Steps

1. Set the missing secrets: `APPLE_TEAM_ID` and `APPLE_KEY_ID`
2. Restart the app: `fly restart`
3. Check logs: `fly logs` - should see app starting successfully
4. Test: `curl https://jose-long-morning-2431.fly.dev/api/books`

