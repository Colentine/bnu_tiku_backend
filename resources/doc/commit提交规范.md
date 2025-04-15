<type>(<scope>): <subject>

<body> （可选）

<footer> （可选）


类型	       说明
feat	   添加新功能
fix	       修复 bug
docs	   仅修改文档
style	   修改代码格式（不影响逻辑）
refactor   代码重构（既不是新功能也不是修复 bug）
test	   添加或修改测试
chore	   构建过程或辅助工具的变动
perf	   提升性能的修改

- 示例1
feat(auth): add JWT token verification

增加了基于 JWT 的身份验证逻辑，确保 API 安全性。

BREAKING CHANGE: 用户必须在请求中添加 Authorization 头

- 示例2
fix(login): 修复无法登录的问题

修复了当用户名中包含特殊字符时登录失败的问题。

- 示例3

docs(readme): 添加项目启动方式说明


