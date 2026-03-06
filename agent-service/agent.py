import json
from typing import Any

from dotenv import load_dotenv

load_dotenv()

from langchain_anthropic import ChatAnthropic
from langchain.agents import create_tool_calling_agent, AgentExecutor
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.tools import BaseTool

from models import LogEvent, Anomaly, AnalyzeResponse
from tools import lookup_known_error, classify_severity


SYSTEM_PROMPT: str = """You are a log anomaly detection agent. You will be given a numbered batch of application logs.
Your job is to:
1. Go through EVERY log entry one by one — do not skip any.
2. For EACH entry, use classify_severity and lookup_known_error tools.
3. After analyzing ALL entries, return ONLY a JSON object in this exact format with no extra text:
{{
  "anomalies": [
    {{
      "severity": "HIGH|MEDIUM|LOW",
      "affectedService": "<service name>",
      "summary": "<brief description>",
      "suggestedAction": "<what to do>"
    }}
  ]
}}
Only include entries that are WARN or ERROR level. If there are no anomalies, return {{"anomalies": []}}
You MUST process every single log entry before returning the final JSON."""


def _build_agent_executor() -> AgentExecutor:
    tools: list[BaseTool] = [lookup_known_error, classify_severity]

    prompt: ChatPromptTemplate = ChatPromptTemplate.from_messages([
        ("system", SYSTEM_PROMPT),
        ("human", "{input}"),
        ("placeholder", "{agent_scratchpad}"),
    ])

    llm: ChatAnthropic = ChatAnthropic(
    model_name="claude-haiku-4-5",
    temperature=0,
    timeout=None,
    stop=None,
)

    agent = create_tool_calling_agent(llm, tools, prompt)
    return AgentExecutor(agent=agent, tools=tools, verbose=True)


# Instantiated once at module load
_agent_executor: AgentExecutor = _build_agent_executor()


def _format_logs(logs: list[LogEvent]) -> str:
    lines: list[str] = [
        f"{i + 1}. [{log.timestamp}] [{log.level}] [{log.service}] {log.message}"
        for i, log in enumerate(logs)
    ]
    return "\n".join(lines)


def _extract_text(output: str | list[Any]) -> str:
    if isinstance(output, str):
        return output
    # output is a list of content blocks e.g. [{"type": "text", "text": "..."}]
    texts: list[str] = [
        block["text"]
        for block in output
        if isinstance(block, dict) and block.get("type") == "text"
    ]
    return "\n".join(texts)


def _parse_response(raw: str | list[Any]) -> AnalyzeResponse:
    text: str = _extract_text(raw)
    clean: str = (
        text.strip()
        .removeprefix("```json")
        .removeprefix("```")
        .removesuffix("```")
        .strip()
    )
    data: dict[str, Any] = json.loads(clean)
    return AnalyzeResponse(**data)


def analyze_logs(logs: list[LogEvent]) -> AnalyzeResponse:
    log_text: str = _format_logs(logs)
    result: dict[str, Any] = _agent_executor.invoke(
        {"input": f"Analyze these logs:\n{log_text}"}
    )
    return _parse_response(result["output"])