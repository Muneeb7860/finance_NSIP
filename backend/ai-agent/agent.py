import asyncio
import logging
import textwrap
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

from dotenv import load_dotenv
from livekit.agents import (
    Agent,
    AgentSession,
    JobContext,
    WorkerOptions,
    cli,
)
from livekit.plugins import google, silero

load_dotenv()

logger = logging.getLogger("nsip-agent")
logger.setLevel(logging.INFO)

# ─── Health Server ────────────────────────────────────────────────────────────
# Kubernetes readiness/liveness probes require an HTTP endpoint.
# We run a minimal HTTP server in a background thread on port 8091.

class _HealthHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path in ("/health", "/actuator/health"):
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(b'{"status":"UP"}')
        else:
            self.send_response(404)
            self.end_headers()

    def log_message(self, format, *args):
        pass  # Suppress noisy access logs

def _start_health_server(port: int = 8091):
    server = HTTPServer(("0.0.0.0", port), _HealthHandler)
    thread = threading.Thread(target=server.serve_forever, daemon=True)
    thread.start()
    logger.info(f"Health server started on port {port}")

# ─── NSIP AI Advisor Agent ────────────────────────────────────────────────────

class Assistant(Agent):
    def __init__(self) -> None:
        super().__init__(
            llm=google.LLM(model="gemini-1.5-flash"),
            instructions=textwrap.dedent(
                """\
                You are the National Social Insurance Platform (NSIP) Advisor. 
                You help citizens with their claims, contributions, and social insurance benefits. 
                Be professional, empathetic, and clear. You use Gemini 1.5 Flash to process multimodal input.
                """
            ),
        )

async def entrypoint(ctx: JobContext):
    logger.info(f"Connecting to room {ctx.room.name}")
    await ctx.connect()

    session = AgentSession(
        stt=google.STT(),
        tts=google.TTS(),
        vad=silero.VAD.load(),
    )

    await session.start(
        agent=Assistant(),
        room=ctx.room,
    )

    await session.say("Hello, I am your NSIP Advisor. How can I assist you today?", allow_interruptions=True)

if __name__ == "__main__":
    _start_health_server(port=8091)
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint))
