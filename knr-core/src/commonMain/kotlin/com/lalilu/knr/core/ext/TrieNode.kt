package com.lalilu.knr.core.ext

/**
 * 路由树节点
 */
class TrieNode<T>(
    val path: String,               // 当前路径片段（如 "user"）
    var isEnd: Boolean = false,     // 是否为路由终点
    var pattern: String = "",       // 完整路由模式（如 "/user/{id}"）
    val isWild: Boolean = false,    // 是否为通配节点（含 { }）
    var route: T? = null            // 存储对应的Screen对象
) {
    val children = mutableMapOf<String, TrieNode<T>>() // 静态子节点
    var wildChild: TrieNode<T>? = null                 // 通配子节点（唯一）

    // 插入路由路径
    fun insert(pattern: String, parts: List<String>, route: T, depth: Int) {
        if (depth == parts.size) {
            this.isEnd = true
            this.pattern = pattern
            this.route = route
            return
        }

        val part = parts[depth]
        val isWild = part.startsWith("{") && part.endsWith("}")

        if (isWild) {
            if (wildChild?.path != part) {
                wildChild = TrieNode(part, isWild = true)
            }
            wildChild!!.insert(pattern, parts, route, depth + 1)
        } else {
            val child = children.getOrPut(part) { TrieNode(part) }
            child.insert(pattern, parts, route, depth + 1)
        }
    }

    // 搜索路由路径
    fun search(parts: List<String>, depth: Int): Pair<TrieNode<T>, Map<String, String>>? {
        if (depth == parts.size) {
            return if (isEnd) this to emptyMap() else null
        }

        val part = parts[depth]
        children[part]?.let { child ->
            return child.search(parts, depth + 1) ?: wildChild?.search(parts, depth + 1)
        }

        return wildChild?.let { wild ->
            wild.search(parts, depth + 1)?.let { (node, params) ->
                // 提取通配符参数名
                val paramName = wild.path.removeSurrounding("{", "}")
                node to (params + (paramName to part))
            }
        }
    }

    companion object {
        fun <T> createRootNode(): TrieNode<T> = TrieNode("")
    }
}