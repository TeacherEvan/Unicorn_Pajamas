"""
RepoSync - GitHub to Hugging Face Repository Synchronization
This module handles the core logic for cloning GitHub repositories
and uploading them to Hugging Face.
"""

from huggingface_hub import HfApi
import subprocess
import shutil
import os
import tempfile
import logging
import re
from urllib.parse import urlparse

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class RepoSyncError(Exception):
    """Custom exception for repository sync errors"""
    pass


def sync_repos(github_url, hf_repo_id, hf_token, repo_type="model"):
    """
    Synchronize a GitHub repository to Hugging Face.
    
    Args:
        github_url (str): The GitHub repository URL to clone
        hf_repo_id (str): The Hugging Face repository ID (format: username/repo-name)
        hf_token (str): Hugging Face authentication token
        repo_type (str): Type of HF repository - "model", "dataset", or "space"
    
    Returns:
        dict: Status information about the sync operation
    
    Raises:
        RepoSyncError: If any step of the sync process fails
    """
    temp_base = None
    local_path = None
    
    try:
        # Validate inputs
        if not github_url or not hf_repo_id or not hf_token:
            raise RepoSyncError("Missing required parameters")
        
        if repo_type not in ["model", "dataset", "space"]:
            raise RepoSyncError(f"Invalid repo_type: {repo_type}")
        
        logger.info(f"Starting sync: {github_url} -> {hf_repo_id}")
        
        # 1. Clone GitHub repository into a subdirectory
        temp_base = tempfile.mkdtemp(prefix="reposync_")
        local_path = os.path.join(temp_base, "repo")
        logger.info(f"Created temp directory: {temp_base}")
        
        # 2. Clone GitHub repository
        logger.info("Cloning GitHub repository...")
        clone_result = subprocess.run(
            ["git", "clone", "--depth", "1", github_url, local_path],
            capture_output=True,
            text=True,
            timeout=300  # 5 minute timeout
        )
        
        if clone_result.returncode != 0:
            raise RepoSyncError(f"Git clone failed: {clone_result.stderr}")
        
        logger.info("Clone completed successfully")
        
        # 3. Remove .git directory to avoid conflicts
        git_dir = os.path.join(local_path, ".git")
        if os.path.exists(git_dir):
            shutil.rmtree(git_dir)
            logger.info("Removed .git directory")
        
        # 4. Initialize Hugging Face API
        logger.info("Initializing Hugging Face API...")
        api = HfApi(token=hf_token)
        
        # 5. Create repository if it doesn't exist
        try:
            api.create_repo(
                repo_id=hf_repo_id,
                repo_type=repo_type,
                exist_ok=True,
                private=False
            )
            logger.info(f"Repository {hf_repo_id} ready")
        except Exception as e:
            logger.warning(f"Repo creation warning: {str(e)}")
        
        # 6. Upload folder to Hugging Face
        logger.info("Uploading to Hugging Face...")
        api.upload_folder(
            folder_path=local_path,
            repo_id=hf_repo_id,
            repo_type=repo_type,
            commit_message=f"Synced from {github_url}"
        )
        
        logger.info("Upload completed successfully")
        
        return {
            "status": "success",
            "message": f"Successfully synced {github_url} to {hf_repo_id}",
            "github_url": github_url,
            "hf_repo_id": hf_repo_id,
            "repo_type": repo_type
        }
        
    except subprocess.TimeoutExpired:
        raise RepoSyncError("Git clone operation timed out")
    except Exception as e:
        error_msg = f"Sync failed: {str(e)}"
        logger.error(error_msg)
        raise RepoSyncError(error_msg)
    
    finally:
        # 7. Cleanup temporary directory
        if temp_base and os.path.exists(temp_base):
            try:
                shutil.rmtree(temp_base)
                logger.info("Cleaned up temporary directory")
            except Exception as e:
                logger.warning(f"Cleanup warning: {str(e)}")


def validate_github_url(url):
    """
    Validate that a URL is a valid GitHub repository URL.
    
    Args:
        url (str): The URL to validate
    
    Returns:
        bool: True if valid, False otherwise
    """
    if not url:
        return False
    
    try:
        # Parse the URL to extract the domain
        if url.startswith("git@"):
            # SSH format: git@github.com:user/repo.git
            if not url.startswith("git@github.com:"):
                return False
            # Validate that there's a path after the colon
            path = url.split(":", 1)[1]
            return bool(path and "/" in path)
        else:
            # HTTPS/HTTP format
            parsed = urlparse(url)
            # Check that the domain is github.com (not just contains it)
            if parsed.netloc.lower() not in ["github.com", "www.github.com"]:
                return False
            # Validate path format: /user/repo or /user/repo.git
            path = parsed.path.strip("/")
            parts = path.split("/")
            return len(parts) >= 2 and all(part for part in parts[:2])
    except Exception:
        return False


def validate_hf_repo_id(repo_id):
    """
    Validate that a Hugging Face repository ID is in the correct format.
    
    Args:
        repo_id (str): The repository ID to validate
    
    Returns:
        bool: True if valid, False otherwise
    """
    if not repo_id or "/" not in repo_id:
        return False
    
    parts = repo_id.split("/")
    return len(parts) == 2 and all(part.strip() for part in parts)
