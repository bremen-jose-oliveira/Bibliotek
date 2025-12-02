# Set Apple Private Key as Fly.io Secret

## Problem
The private key file `AuthKey_3NJ23GPMBU.p8` is in `.gitignore`, so it's not in GitHub and won't be in the Docker image. This causes `invalid_client` errors.

## Solution
Store the private key as a Fly.io secret (environment variable).

## Step 1: Get Your Private Key Content

Your private key file is at: `src/main/resources/AuthKey_3NJ23GPMBU.p8`

The content is:
```
-----BEGIN PRIVATE KEY-----
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgemc8O1ppqmIbZqQa
yZdC0y0Z71T8ABw6dg+u1T6YmMKgCgYIKoZIzj0DAQehRANCAARvF+yixjidAcoD
lEo3ybx3c5njHh3sUrWFSyeMSQtZZpRM1Q5x9vKQyeFr6Shv7UAyt5J9o1KDrCot
1CyAWClG
-----END PRIVATE KEY-----
```

## Step 2: Set as Fly.io Secret

**Important:** Include the entire key including BEGIN and END lines!

```powershell
fly secrets set APPLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgemc8O1ppqmIbZqQa
yZdC0y0Z71T8ABw6dg+u1T6YmMKgCgYIKoZIzj0DAQehRANCAARvF+yixjidAcoD
lEo3ybx3c5njHh3sUrWFSyeMSQtZZpRM1Q5x9vKQyeFr6Shv7UAyt5J9o1KDrCot
1CyAWClG
-----END PRIVATE KEY-----" -a jose-long-morning-2431
```

## Step 3: Verify Secrets

```powershell
fly secrets list -a jose-long-morning-2431
```

Should show:
- `APPLE_TEAM_ID`
- `APPLE_KEY_ID`
- `APPLE_PRIVATE_KEY` ← NEW!

## Step 4: Restart the App

```powershell
fly restart -a jose-long-morning-2431
```

## Step 5: Test

After restart, try Apple login again. It should work now!

## Alternative: Set via Fly.io Dashboard

1. Go to: https://fly.io/dashboard
2. Select your app: **jose-long-morning-2431**
3. Go to **Secrets** tab
4. Click **Add secret**
5. Key: `APPLE_PRIVATE_KEY`
6. Value: Paste the entire private key (with BEGIN and END lines)
7. Save

## Note

The code now supports both:
- Environment variable `APPLE_PRIVATE_KEY` (for production/Fly.io)
- Classpath file `AuthKey_3NJ23GPMBU.p8` (for local development)

This way the private key is secure and not in Git!

