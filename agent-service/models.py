from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class LogEvent(BaseModel):
    timestamp: datetime
    level: str
    service: str
    message: str
    traceId: Optional[str] = None

class AnalyzeRequest(BaseModel):
    logs: list[LogEvent]

class Anomaly(BaseModel):
    severity: str          # HIGH, MEDIUM, LOW
    affectedService: str
    summary: str
    suggestedAction: str

class AnalyzeResponse(BaseModel):
    anomalies: list[Anomaly]