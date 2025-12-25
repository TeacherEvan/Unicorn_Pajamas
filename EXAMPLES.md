# Example Configuration

This document provides example configurations for common use cases.

## Basic Setup

### 1. Public Repository Sync

Syncing a public GitHub repository to Hugging Face:

**GitHub URL**: `https://github.com/huggingface/transformers.git`  
**Hugging Face URL**: `https://huggingface.co/username/transformers`

**Required Tokens**:
- GitHub: Public access (optional for public repos, but recommended)
- Hugging Face: Write access token

### 2. Private Repository Sync

Syncing a private GitHub repository to Hugging Face:

**GitHub URL**: `https://github.com/username/private-repo.git`  
**Hugging Face URL**: `https://huggingface.co/username/private-model`

**Required Tokens**:
- GitHub: PAT with `repo` scope
- Hugging Face: Write access token

### 3. Dataset Sync

Syncing a dataset repository:

**GitHub URL**: `https://github.com/username/dataset.git`  
**Hugging Face URL**: `https://huggingface.co/datasets/username/my-dataset`

**Required Tokens**:
- GitHub: Public or repo scope
- Hugging Face: Write access token

## Token Configuration

### GitHub Personal Access Token

1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Select scopes:
   - For public repos: `public_repo`
   - For private repos: `repo` (full control)
4. Set expiration (recommended: 90 days)
5. Generate and copy token
6. Paste in app Settings

**Example Token Format**: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

### Hugging Face Token

1. Go to https://huggingface.co/settings/tokens
2. Click "New token"
3. Name: "Unicorn Pajamas Android"
4. Role: "write"
5. Create and copy token
6. Paste in app Settings

**Example Token Format**: `hf_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

## URL Formats

### GitHub URLs

Supported formats:
```
https://github.com/username/repo.git
https://github.com/username/repo
git@github.com:username/repo.git
```

Recommended: HTTPS format with `.git` extension

### Hugging Face URLs

Supported formats:
```
https://huggingface.co/username/repo
https://huggingface.co/datasets/username/dataset
https://huggingface.co/username/repo.git
```

Repository types:
- Models: `https://huggingface.co/username/model-name`
- Datasets: `https://huggingface.co/datasets/username/dataset-name`
- Spaces: `https://huggingface.co/spaces/username/space-name`

## Integration Examples

### Example 1: GitHub Actions Integration

Trigger sync after GitHub push:

```yaml
# .github/workflows/sync-to-hf.yml
name: Sync to Hugging Face

on:
  push:
    branches: [main]

jobs:
  notify:
    runs-on: ubuntu-latest
    steps:
      - name: Send webhook
        run: |
          curl -X POST https://your-webhook-server.com/sync \
            -H "Content-Type: application/json" \
            -d '{
              "github_url": "https://github.com/${{ github.repository }}.git",
              "huggingface_url": "https://huggingface.co/username/repo"
            }'
```

### Example 2: Automated Model Upload

Sync a trained model to Hugging Face:

1. Train model locally
2. Push to GitHub repository
3. Use app to sync to Hugging Face
4. Model available at `https://huggingface.co/username/model-name`

### Example 3: Dataset Mirror

Mirror a dataset from GitHub to Hugging Face:

**GitHub** (source): `https://github.com/username/dataset.git`  
**Hugging Face** (mirror): `https://huggingface.co/datasets/username/dataset`

Configure once in app, sync whenever dataset updates.

### Example 4: Multi-Repository Sync

Sync multiple repositories sequentially:

1. Sync repo 1: ML model code
2. Sync repo 2: Trained weights
3. Sync repo 3: Demo application

Each sync must be configured separately in the app.

## Advanced Configuration

### Repository Structure

Ensure your repository has appropriate structure for Hugging Face:

**For Models**:
```
repo/
├── README.md
├── config.json
├── pytorch_model.bin
└── tokenizer_config.json
```

**For Datasets**:
```
repo/
├── README.md
├── dataset_info.json
└── data/
    ├── train.csv
    └── test.csv
```

**For Spaces**:
```
repo/
├── README.md
├── app.py
└── requirements.txt
```

### Large Files

For repositories with large files (>100MB):

1. Use Git LFS on GitHub
2. Ensure Hugging Face repository supports large files
3. Monitor sync progress in app logs
4. Consider using chunked uploads for very large files

### Git Configuration

Recommended `.gitattributes` for model files:

```gitattributes
*.bin filter=lfs diff=lfs merge=lfs -text
*.h5 filter=lfs diff=lfs merge=lfs -text
*.ckpt filter=lfs diff=lfs merge=lfs -text
*.safetensors filter=lfs diff=lfs merge=lfs -text
```

### Branch Selection

The app syncs all branches by default. To sync specific branch:

**Note**: Current version syncs all branches. For single branch, ensure GitHub repository has only the desired branch, or manually configure on Hugging Face after first sync.

## Troubleshooting Examples

### Problem: Authentication Failed

**Symptoms**: "Auth failed" error in logs

**Solutions**:
1. Verify tokens in Settings
2. Check token scopes/permissions
3. Ensure tokens haven't expired
4. Test tokens manually:
   ```bash
   # Test GitHub token
   curl -H "Authorization: token YOUR_GITHUB_TOKEN" https://api.github.com/user
   
   # Test Hugging Face token
   curl -H "Authorization: Bearer YOUR_HF_TOKEN" https://huggingface.co/api/whoami-v2
   ```

### Problem: Large Repository Timeout

**Symptoms**: Sync fails for large repositories

**Solutions**:
1. Ensure stable network connection
2. Use WiFi instead of cellular
3. Sync during off-peak hours
4. Consider repository size limitations

### Problem: Push Rejected

**Symptoms**: "Push rejected" or "non-fast-forward" error

**Solutions**:
1. Ensure Hugging Face repository is empty for first sync
2. Check for conflicting commits on Hugging Face
3. Use force push (app does this by default)
4. Manually resolve conflicts if needed

### Problem: Invalid URL

**Symptoms**: "Invalid URL" error

**Solutions**:
1. Verify URL format
2. Ensure repository exists
3. Check for typos
4. Use HTTPS format with `.git` extension

## Performance Tips

### Network Optimization

1. Use WiFi for large repositories
2. Sync during low-traffic periods
3. Monitor data usage
4. Consider network bandwidth limits

### Storage Management

1. App uses cache directory (auto-cleared by Android)
2. Temporary clones deleted after sync
3. Monitor device storage space
4. Large repos require adequate free space

### Battery Optimization

1. Plug in device for large syncs
2. Disable battery optimization for app if sync interrupts
3. Use Android's adaptive battery features
4. Monitor battery usage in settings

## Best Practices

### Security
- Rotate tokens every 90 days
- Use separate tokens per device/app
- Never share tokens
- Review repository access before syncing

### Reliability
- Test with small repositories first
- Verify sync completion in logs
- Check Hugging Face repository after sync
- Keep app updated

### Organization
- Use descriptive repository names
- Maintain README files
- Tag releases appropriately
- Document model/dataset metadata

### Monitoring
- Review sync logs regularly
- Monitor repository sizes
- Track sync frequency
- Check for errors or warnings

## Sample Repositories

### Test Repository (Small)

**GitHub**: `https://github.com/huggingface/pytorch-image-models.git`  
**Size**: ~50MB  
**Sync Time**: ~1-2 minutes

Use this to test the app functionality.

### Production Repository (Large)

**GitHub**: `https://github.com/huggingface/transformers.git`  
**Size**: ~200MB  
**Sync Time**: ~5-10 minutes

Use this to test with realistic production sizes.

## Support

For configuration help:
1. Check this document
2. Review README.md
3. Check API_INTEGRATION.md for advanced usage
4. Open GitHub issue for specific problems
