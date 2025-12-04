router.addRoute("/api/users", "exact-users");      // exact match
router.addRoute("/api/*/test", "wildcard-test");   // wildcard
router.addRoute("/api/{version}/docs", "param-docs"); // param
```

### Trie Structure:
```
                            [ROOT]
                         children = {
                           "api" → TrieNode
                         }
                              │
                              ▼
                         [api node]
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
children = {          wildcardChild         paramChild
"users"→TrieNode      = TrieNode           = TrieNode
}                           │               paramName = "version"
│                      │                     │
▼                      ▼                     ▼
[users node]           [* node]            [{version} node]
isEnd = true          children = {         children = {
result =               "test"→TrieNode      "docs"→TrieNode
"exact-users"         }                    }
│                     │
▼                     ▼
[test node]          [docs node]
isEnd = true         isEnd = true
result =             result =
"wildcard-test"      "param-docs"
```

---

## Lookup Process Explained

### Query: `/api/v1/test`
```
Step 1: segments = ["", "api", "v1", "test"]

Step 2: At ROOT, segment = "api"
→ children.get("api") ✓
→ Move to [api node]

Step 3: At [api node], segment = "v1"
→ children.get("v1") ✗ (no exact match)
→ paramChild exists? ✓ Try it...
└── params.put("version", "v1")
└── Continue search... eventually fails (no "test" under docs path)
└── Backtrack, remove param
→ wildcardChild exists? ✓ Try it...
└── Move to [* node]

Step 4: At [* node], segment = "test"
→ children.get("test") ✓
→ Move to [test node]

Step 5: At [test node], no more segments
→ isEnd = true ✓
→ Return "wildcard-test"
```

---

## Priority Order During Lookup
```
1. EXACT    →  children.get(segment)     [Highest]
2. PARAM    →  paramChild                [Middle]
3. WILDCARD →  wildcardChild             [Lowest]