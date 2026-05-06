import asyncio
import logging
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, JobProcess
from livekit.agents.llm import ChatContext, ChatMessage
from livekit.agents.voice_assistant import VoiceAssistant
from livekit.plugins import google

load_dotenv()

logger = logging.getLogger("nsip-agent")
logger.setLevel(logging.INFO)

async def entrypoint(ctx: JobContext):
    logger.info(f"Connecting to room {ctx.room.name}")
    await ctx.connect()

    chat_context = ChatContext(
        messages=[
            ChatMessage(
                role="system",
                content="You are the National Social Insurance Platform (NSIP) Advisor. "
                        "You help citizens with their claims, contributions, and social insurance benefits. "
                        "Be professional, empathetic, and clear. You use Gemini 1.5 Flash to process multimodal input."
            )
        ]
    )

    # Using Gemini 1.5 Flash via Google Plugin
    model = google.LLM(model="gemini-1.5-flash")

    assistant = VoiceAssistant(
        vad=google.VAD(), # Voice Activity Detection
        stt=google.STT(), # Speech to Text
        llm=model,
        tts=google.TTS(), # Text to Speech
        chat_ctx=chat_context,
    )

    assistant.start(ctx.room)
    await assistant.say("Hello, I am your NSIP Advisor. How can I assist you today?", allow_interruptions=True)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint))
