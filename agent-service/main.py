from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse

from models import AnalyzeRequest, AnalyzeResponse
from agent import analyze_logs


app: FastAPI = FastAPI(
    title="Log Anomaly Agent",
    description="Receives batches of application logs and returns detected anomalies.",
    version="1.0.0",
)


@app.get("/health", response_model=dict[str, str])
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/analyze", response_model=AnalyzeResponse)
def analyze(request: AnalyzeRequest) -> AnalyzeResponse:
    if not request.logs:
        raise HTTPException(status_code=400, detail="logs list must not be empty")
    try:
        return analyze_logs(request.logs)
    except ValueError as e:
        raise HTTPException(status_code=422, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))