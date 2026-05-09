import asyncio
import logging
import textwrap

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
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint))
