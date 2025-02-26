from fastapi import APIRouter
from pydantic import BaseModel
import json
import os

router = APIRouter()

TIMESTAMP_FILE = "timestamps.json"

def load_timestamps():
    if os.path.exists(TIMESTAMP_FILE):
        with open(TIMESTAMP_FILE, "r", encoding="utf-8") as file:
            return json.load(file)
    return {}

def save_timestamps(data):
    with open(TIMESTAMP_FILE, "w", encoding="utf-8") as file:
        json.dump(data, file, indent=4)

class UpdateRequest(BaseModel):
    buttonId: str
    timestamp: str

@router.post("/save-update-timestamp")
async def save_update_timestamp(request: UpdateRequest):
    data = load_timestamps()
    data[request.buttonId] = request.timestamp
    save_timestamps(data)
    return {"success": True, "message": "Timestamp saved"}

@router.get("/get-update-timestamps")
async def get_update_timestamps():
    data = load_timestamps()
    return data
