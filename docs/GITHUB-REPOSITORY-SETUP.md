# GitHub Repository Setup Guide

This guide walks you through configuring your GitHub repository for optimal CI/CD and security features.

## Required Setup

### 1. Enable Dependency Graph

The Dependency Graph feature is required for the Dependency Review workflow to function.

**Steps:**
1. Go to your repository on GitHub
2. Click **Settings** tab
3. Navigate to **Security & analysis** (left sidebar)
4. Find **Dependency graph** section
5. Click **Enable** button

**What it does:**
- Tracks your project's dependencies
- Identifies security vulnerabilities
- Powers the Dependency Review workflow
- Works with Dependabot

### 2. Enable Dependabot Alerts (Recommended)

Dependabot alerts notify you about vulnerabilities in dependencies.

**Steps:**
1. In **Settings** → **Security & analysis**
2. Find **Dependabot alerts** section
3. Click **Enable** button

**Benefits:**
- Automatic vulnerability detection
- Email notifications for security issues
- Integration with GitHub Security tab

### 3. Enable Dependabot Security Updates (Recommended)

Automatically creates PRs to update vulnerable dependencies.

**Steps:**
1. In **Settings** → **Security & analysis**
2. Find **Dependabot security updates** section
3. Click **Enable** button

**Benefits:**
- Automated security patches
- Reduces manual dependency updates
- Keeps your project secure

### 4. Configure Branch Protection Rules

Protect your main branch from direct pushes and ensure CI passes.

**Steps:**
1. Go to **Settings** → **Branches**
2. Click **Add branch protection rule**
3. Branch name pattern: `main`
4. Enable the following:

**Required Settings:**
- ✅ **Require a pull request before merging**
  - Require approvals: 1
- ✅ **Require status checks to pass before merging**
  - Status checks to require:
    - `build` (from Android CI)
    - `code-quality` (from PR checks)
    - `size-check` (from PR checks)
- ✅ **Require conversation resolution before merging**
- ✅ **Require linear history** (optional but recommended)

**Optional but Recommended:**
- ✅ **Require deployments to succeed before merging**
- ✅ **Do not allow bypassing the above settings**
- ✅ **Restrict who can push to matching branches**

### 5. Add Repository Secrets (For Release Signing)

If you want to sign release APKs, add these secrets:

**Steps:**
1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Add the following secrets:

**Required Secrets for APK Signing:**

| Secret Name | Description |
|-------------|-------------|
| `SIGNING_KEY` | Base64-encoded keystore file |
| `KEY_ALIAS` | Key alias from keystore |
| `KEY_STORE_PASSWORD` | Keystore password |
| `KEY_PASSWORD` | Key password |

**How to generate:**
```bash
# Generate keystore
keytool -genkey -v -keystore release-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release-key

# Encode to base64
base64 release-keystore.jks > keystore.base64
```

Copy the contents of `keystore.base64` and paste as the `SIGNING_KEY` secret.

### 6. Enable GitHub Actions

Ensure GitHub Actions is enabled for your repository.

**Steps:**
1. Go to **Settings** → **Actions** → **General**
2. Under **Actions permissions**, select:
   - ✅ **Allow all actions and reusable workflows**
3. Under **Workflow permissions**, select:
   - ✅ **Read and write permissions**
   - ✅ **Allow GitHub Actions to create and approve pull requests**

### 7. Configure Dependabot (Already Done)

The `.github/dependabot.yml` file is already configured. Verify it's active:

**Check:**
1. Go to **Insights** → **Dependency graph** → **Dependabot**
2. Verify Dependabot is checking for updates

## Optional Features

### Enable Code Scanning

For advanced security analysis:

1. **Settings** → **Security & analysis**
2. **Code scanning** → **Set up** → **Advanced**
3. Use the default CodeQL workflow

### Enable Secret Scanning

Detects accidentally committed secrets:

1. **Settings** → **Security & analysis**
2. **Secret scanning** → **Enable**

### Enable Push Protection

Prevents pushing commits with secrets:

1. **Settings** → **Security & analysis**
2. **Push protection** → **Enable**

## Verification Checklist

After setup, verify everything is working:

- [ ] Dependency graph shows dependencies
- [ ] Dependabot is active (check Insights tab)
- [ ] GitHub Actions workflows run successfully
- [ ] Branch protection prevents direct pushes to main
- [ ] PR checks must pass before merging
- [ ] Release workflow can access secrets (if configured)

## Troubleshooting

### Dependency Graph Not Showing Data

**Solution:**
- It may take a few minutes after enabling
- Ensure you've pushed code after enabling
- Check that `build.gradle.kts` files are present

### Workflows Not Running

**Solution:**
- Check **Settings** → **Actions** → **General**
- Ensure actions are enabled
- Check workflow syntax in `.github/workflows/`

### Branch Protection Not Working

**Solution:**
- Verify the branch name pattern matches exactly
- Check that required status checks exist
- Push a PR to test the protection

### Dependabot PRs Not Created

**Solution:**
- Check `.github/dependabot.yml` is valid
- Ensure dependency graph is enabled
- Wait up to 1 week for first PR (runs weekly)

## Security Best Practices

1. **Never commit secrets** to the repository
2. **Use GitHub Secrets** for sensitive data
3. **Enable all security features** mentioned above
4. **Review Dependabot PRs** promptly
5. **Keep dependencies updated** regularly
6. **Use branch protection** on main branch
7. **Require code reviews** for all changes

## Additional Resources

- [GitHub Security Features](https://docs.github.com/en/code-security)
- [Dependency Graph Documentation](https://docs.github.com/en/code-security/supply-chain-security/understanding-your-software-supply-chain/about-the-dependency-graph)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Branch Protection Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

## Support

If you encounter issues with repository setup:
1. Check GitHub Status page
2. Review GitHub documentation
3. Open an issue in this repository

---

Last updated: 2025-01-23
