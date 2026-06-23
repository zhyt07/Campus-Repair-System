"""
通过 GitHub API 将 backend/ 目录下的所有文件提交到 zhyt07/Campus-Repair-System 仓库

使用方法:
    python push_to_github.py <GITHUB_TOKEN>

获取 Token: https://github.com/settings/tokens
权限需要: repo (Full control of private repositories)
"""
import os
import sys
import json
import base64
import requests

REPO_OWNER = "zhyt07"
REPO_NAME = "Campus-Repair-System"
BRANCH = "main"
BACKEND_DIR = os.path.dirname(os.path.abspath(__file__))

API_BASE = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}"

def get_headers(token):
    return {
        "Authorization": f"token {token}",
        "Accept": "application/vnd.github.v3+json"
    }

def get_latest_commit_sha(token):
    """获取最新提交的 SHA"""
    url = f"{API_BASE}/git/ref/heads/{BRANCH}"
    resp = requests.get(url, headers=get_headers(token))
    if resp.status_code == 200:
        return resp.json()["object"]["sha"]
    raise Exception(f"获取分支信息失败: {resp.status_code} {resp.text}")

def get_tree_sha(commit_sha, token):
    """获取提交的 tree SHA"""
    url = f"{API_BASE}/git/commits/{commit_sha}"
    resp = requests.get(url, headers=get_headers(token))
    if resp.status_code == 200:
        return resp.json()["tree"]["sha"]
    raise Exception(f"获取提交信息失败: {resp.status_code} {resp.text}")

def create_blob(file_path, token):
    """为文件创建 blob"""
    with open(file_path, "rb") as f:
        content = f.read()
    
    content_b64 = base64.b64encode(content).decode("utf-8")
    
    url = f"{API_BASE}/git/blobs"
    data = {
        "content": content_b64,
        "encoding": "base64"
    }
    resp = requests.post(url, headers=get_headers(token), json=data)
    if resp.status_code == 201:
        return resp.json()["sha"]
    raise Exception(f"创建 blob 失败 ({file_path}): {resp.status_code} {resp.text}")

def collect_files(base_dir):
    """收集所有需要上传的文件"""
    files = []
    skip_dirs = {".git", "__pycache__", "target", "node_modules", "uploads"}
    skip_files = {"push_to_github.py"}
    
    for root, dirs, filenames in os.walk(base_dir):
        # 跳过不需要的目录
        dirs[:] = [d for d in dirs if d not in skip_dirs]
        
        for filename in filenames:
            if filename in skip_files:
                continue
            file_path = os.path.join(root, filename)
            rel_path = os.path.relpath(file_path, base_dir).replace("\\", "/")
            files.append((file_path, rel_path))
    
    return files

def create_tree(base_tree_sha, files, token):
    """创建新的 tree"""
    tree_items = []
    for file_path, rel_path in files:
        print(f"  处理: {rel_path}")
        blob_sha = create_blob(file_path, token)
        tree_items.append({
            "path": f"backend/{rel_path}",
            "mode": "100644",
            "type": "blob",
            "sha": blob_sha
        })
    
    url = f"{API_BASE}/git/trees"
    data = {
        "base_tree": base_tree_sha,
        "tree": tree_items
    }
    resp = requests.post(url, headers=get_headers(token), json=data)
    if resp.status_code == 201:
        return resp.json()["sha"]
    raise Exception(f"创建 tree 失败: {resp.status_code} {resp.text}")

def create_commit(parent_sha, tree_sha, token):
    """创建新的 commit"""
    url = f"{API_BASE}/git/commits"
    data = {
        "message": "feat: 添加完整 Spring Boot 后端代码\n\n- Spring Boot 2.7 + MyBatis-Plus + MySQL 8.0\n- 包含全部 Entity、Mapper、Service、Controller\n- 实现智能派单算法（匹配度评分）\n- 包含 JWT 认证、BCrypt 加密、Redis 缓存\n- 包含数据库初始化脚本",
        "tree": tree_sha,
        "parents": [parent_sha]
    }
    resp = requests.post(url, headers=get_headers(token), json=data)
    if resp.status_code == 201:
        return resp.json()["sha"]
    raise Exception(f"创建 commit 失败: {resp.status_code} {resp.text}")

def update_ref(commit_sha, token):
    """更新分支引用"""
    url = f"{API_BASE}/git/refs/heads/{BRANCH}"
    data = {
        "sha": commit_sha,
        "force": False
    }
    resp = requests.patch(url, headers=get_headers(token), json=data)
    if resp.status_code == 200:
        return resp.json()
    raise Exception(f"更新分支失败: {resp.status_code} {resp.text}")

def main():
    if len(sys.argv) < 2:
        print("用法: python push_to_github.py <GITHUB_TOKEN>")
        print("获取 Token: https://github.com/settings/tokens (需要 repo 权限)")
        sys.exit(1)
    
    token = sys.argv[1]
    
    print(f"仓库: {REPO_OWNER}/{REPO_NAME}")
    print(f"分支: {BRANCH}")
    print()
    
    # 收集文件
    print("正在收集文件...")
    files = collect_files(BACKEND_DIR)
    print(f"共 {len(files)} 个文件")
    print()
    
    # 获取基础信息
    print("正在获取仓库信息...")
    latest_sha = get_latest_commit_sha(token)
    base_tree_sha = get_tree_sha(latest_sha, token)
    print(f"最新 commit: {latest_sha[:7]}")
    print()
    
    # 创建 tree
    print("正在创建文件 tree...")
    new_tree_sha = create_tree(base_tree_sha, files, token)
    print(f"Tree SHA: {new_tree_sha[:7]}")
    print()
    
    # 创建 commit
    print("正在创建 commit...")
    new_commit_sha = create_commit(latest_sha, new_tree_sha, token)
    print(f"Commit SHA: {new_commit_sha[:7]}")
    print()
    
    # 更新分支
    print("正在推送到远程仓库...")
    update_ref(new_commit_sha, token)
    print()
    print("✅ 推送成功！")
    print(f"查看: https://github.com/{REPO_OWNER}/{REPO_NAME}")

if __name__ == "__main__":
    main()
