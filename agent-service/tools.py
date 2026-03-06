from langchain_core.tools import tool

KNOWN_ERRORS: dict[str, str] = {
    "DB connection timeout": (
        "Likely a connection pool exhaustion or DB overload. "
        "Check pool size and DB metrics."
    ),
    "NullPointerException": (
        "A null reference was dereferenced. "
        "Check recent code changes in the affected service."
    ),
    "JWT validation failed": (
        "Auth token issue. "
        "Could be clock skew, wrong secret, or expired token."
    ),
    "Retry attempt": (
        "Transient failure causing retries. "
        "Check downstream service health."
    ),
    "Stock level critically low": (
        "Inventory warning. "
        "May need restock trigger or alert to ops team."
    ),
}

@tool
def lookup_known_error(message: str) -> str:
    """Look up a log message against a database of known error patterns
    and return remediation advice if found."""
    for pattern, advice in KNOWN_ERRORS.items():
        if pattern.lower() in message.lower():
            return f"Known issue: {advice}"
    return "No known pattern matched. Treat as novel anomaly."


@tool
def classify_severity(level: str, message: str) -> str:
    """Classify the severity of a log event as HIGH, MEDIUM, or LOW
    based on its log level and message content."""
    high_keywords: list[str] = ["timeout", "exception", "failed", "crash"]

    if level == "ERROR":
        if any(kw in message.lower() for kw in high_keywords):
            return "HIGH"
        return "MEDIUM"
    if level == "WARN":
        return "MEDIUM"
    return "LOW"