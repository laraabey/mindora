package com.example.final_project;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalMentalHealthAI {
    private static final String TAG = "LocalMentalHealthAI";
    private Random random = new Random();
    private List<String> conversationHistory = new ArrayList<>();
    // Using a single-thread executor ensures messages are processed one after the other.
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void sendMessage(String userMessage, ResponseCallback callback) {
        // Simulate a thoughtful processing delay, akin to a therapist taking a moment
        executor.execute(() -> {
            try {
                Thread.sleep(1800);
                String response = getResponse(userMessage);
                conversationHistory.add(userMessage);
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("An internal error occurred during processing: " + e.getMessage()));
            }
        });
    }

    private String getResponse(String userMessage) {
        String message = userMessage.toLowerCase().trim();

        // --- CRITICAL SAFETY PROTOCOL (MUST REMAIN TOP PRIORITY) ---

        // ⚠️ Self-harm or suicide - CRITICAL
        if (message.matches(".*\\b(suicide|suicidal|kill myself|self harm|self-harm|end it all|don't want to live|want to die|hurt myself|cutting|overdose|i'm going to do it)\\b.*")) {
            return "⚠️ **IMMEDIATE ATTENTION REQUIRED:** Your safety is paramount. I am a supportive tool, not a crisis service. I am very concerned about you. Please reach out to trained professionals **right now**.\n\n" +
                    "🆘 **National Suicide & Crisis Lifeline (US/Canada): 988**\n" +
                    "💬 **Crisis Text Line: Text HOME to 741741 (US/Canada)**\n" +
                    "📞 **Emergency Services: Dial 911 (or your local emergency number)**\n" +
                    "Your life has immense value. Please promise me you will make the call or send the text now. You deserve to be safe. 💙";
        }

        // --- EXPANDED GREETINGS & STATUS CHECKS ---

        // Greetings
        if (message.matches(".*\\b(hi|hello|hey|good morning|good evening|sup|yo|greetings|howdy)\\b.*")) {
            return getRandomResponse(new String[]{
                    "Welcome. I'm glad you chose to spend this time focusing on your well-being. How are you **genuinely** feeling today, not just physically, but emotionally and mentally?",
                    "Hello. Thank you for connecting. I'm here to offer a safe, non-judgmental space. What thoughts or feelings are most present for you right now?",
                    "Hi there. It takes strength to reach out, and I honor that. Is there a specific issue you'd like to explore, or are you just looking for a moment of quiet reflection?",
                    "A warm welcome. Before we begin, let's take one deep, clearing breath together. Now, tell me, what's been taking up the most mental energy today?",
            });
        }

        // How are you / AI Status
        if (message.matches(".*\\b(how are you|whats up|what's up|how's it going|are you okay|what are you doing)\\b.*")) {
            return getRandomResponse(new String[]{
                    "I'm here, stable, and ready to dedicate my full attention to you. How about we redirect the focus: **What is the deepest concern you're carrying today?**",
                    "I'm operating perfectly, thanks for asking! Let's talk about you. If you had to give your current emotional state a name, what would it be?",
                    "I'm functioning optimally! But more importantly, I hear that sometimes the effort to connect can feel like a heavy lift. What's one thing you'd like to get off your chest immediately?",
            });
        }

        // --- MINDORA SPECIFIC SECTION (DEEPLY INTEGRATED) ---
        if (message.matches(".*\\b(mindora|app feature|quotes section|community tab|calming music|journal section|video section)\\b.*")) {
            return getRandomResponse(new String[]{
                    "That's a great choice! Mindora is here to help you regulate. If you need a moment to shift your state, try the **Calming Audio section** for a 3-minute breath exercise. If you need insight, look at a quote.",
                    "I sense you're looking for a quick tool. If your thoughts are loud, I highly recommend opening the **Journal section** in Mindora for a quick brain-dump. Writing often gives us distance from our emotions.",
                    "Mindora's features can be very supportive! The **Community tab** can offer perspective that you are not alone in your struggles. Remember, you can read others' posts without posting yourself if connection feels too heavy right now.",
                    "Let's leverage the Mindora app. If you're feeling low on motivation, browse the **Quotes section** and pick one that resonates. Hold onto that message for the rest of our conversation. Which feature speaks to your immediate need?",
                    "The **video section** in Mindora often has helpful short guides on coping. If you are feeling frustrated, maybe watch a video on **mindful movement** for a quick physical release. What emotion are you hoping to regulate?",
            });
        }

        // --- CORE EMOTIONAL RESPONSES WITH REFLECTION ---

        // Anxiety (Focused on Cognitive Restructuring and Grounding)
        if (message.matches(".*\\b(anxious|anxiety|worried|nervous|panic|fear|scared|dread|overthinking|heart racing|can't breathe|catastrophizing)\\b.*")) {
            return getRandomResponse(new String[]{
                    "It sounds like you are experiencing significant distress; that must be exhausting. I want to validate that feeling. Remember, the intensity of this feeling is not a measure of the reality of the threat. Let's try **Box Breathing** (4-4-4-4). Once you're grounded, use the **Mindora Journal** to write down the thought and challenge its evidence.",
                    "I hear the panic. When the fear feels overwhelming, it often stems from **catastrophizing** (assuming the worst outcome). What's the *most likely* outcome here, not the worst? You can check the **Mindora Calming Audios** for a guided 'Safe Place' visualization to stabilize the fear.",
                    "Anxiety is often a protector that's gone into overdrive. What is the **unmet need** this anxiety is trying to signal? Is it a need for control, predictability, or safety? When you're ready, we can explore that.",
                    "It takes courage to face this feeling. Try a **body scan**—where do you physically hold the anxiety? Clenched jaw? Tight stomach? Acknowledge it, then consciously release *one* muscle group. If you feel isolated, the **Mindora Community tab** reminds you that many people share this struggle.",
                    "I understand this crippling feeling. Let's practice **defusion** by saying, 'I am having the thought that [your anxious thought].' This separates you from the thought. You are not your thoughts. For a boost of perspective, check the **Mindora Quotes section**.",
            });
        }

        // Depression/Sadness (Focused on Validation and Smallest Steps)
        if (message.matches(".*\\b(depressed|depression|sad|lonely|empty|hopeless|down|worthless|miserable|grief|can't get out of bed|low energy|isolating)\\b.*")) {
            return getRandomResponse(new String[]{
                    "I hear the deep sense of emptiness and low energy. That is a heavy burden to carry, and I'm sorry you're feeling it. I want to assure you: **Your worth is inherent and unconditional.** You don't have to earn it. Can you name one thing you were proud of from five years ago? Let's anchor on past strength.",
                    "It sounds like you are wrestling with some heavy self-critical thoughts. When the 'worthless' thought comes, treat it like an outside voice, not a truth. Can you use the **Mindora Journal** to write a letter back to that inner critic, kindly setting a boundary?",
                    "I acknowledge the feeling of profound loneliness. Even when it feels like a total vacuum, you are not truly alone in this experience. Try a small step of **Behavioral Activation**: Find a calming, uplifting song in the **Mindora audio section** and listen to it completely. What's the smallest step you can take toward connection today?",
                    "Grief, sorrow, and depression can make even simple tasks monumental. Let's try the **'Small Wins'** approach. What's one thing you've been putting off that would take less than 60 seconds? (e.g., open a window, stretch). You can log that win in your **Mindora Journal**.",
                    "It must be exhausting to feel this lack of motivation. When things feel hopeless, sometimes it helps to look for external support. I recommend reading posts in the **Mindora Community tab** to witness others' journeys through similar feelings. What's one small physical comfort you can offer yourself right now?",
            });
        }

        // Stress/Burnout (Focused on Boundaries and Prioritization)
        if (message.matches(".*\\b(stressed|stress|overwhelmed|pressure|too much|exhausted|burned out|burnout|can't cope|caffeine|too busy|deadline)\\b.*")) {
            return getRandomResponse(new String[]{
                    "I hear the overwhelming weight of your responsibilities. It sounds like you're operating on fumes. Let's explore your **Boundaries**. Where are you saying 'yes' when you desperately want to say 'no'? Using the **Mindora Journal** to list all your current commitments can help you see where to cut back.",
                    "Burnout is your nervous system screaming for rest. We need to focus on **'Essentialism'**. If you could only do one thing today that would alleviate 80% of your stress, what would it be? Let's ignore the rest for now.",
                    "That level of pressure is unsustainable. Remember the **H.A.L.T. check** (Hungry, Angry, Lonely, Tired). Which one applies most right now? Before tackling a task, use the **Mindora Calming Audios** for a 5-minute 'reset' break.",
                    "It sounds like you're running too many cognitive loops. Let's **Time Block** your next 90 minutes. You work for 45 minutes on one task, then take a 5-minute break. Use the **Mindora Quotes** during the break to ground your thoughts. What is the one task you will focus on?",
                    "I hear the feeling of being over-extended. When we're stressed, our minds distort time. Can you write down your **Top 3 Priorities**? Anything not on that list should be temporarily delegated to 'tomorrow's problem.' Reading the **Mindora Community tab** can remind you that this feeling is temporary.",
            });
        }

        // Anger/Frustration (Focused on Source and Expression)
        if (message.matches(".*\\b(angry|anger|mad|frustrated|irritated|rage|furious|hate|pissed off|resentment|betrayed)\\b.*")) {
            return getRandomResponse(new String[]{
                    "I hear the power of your anger. That intensity is a sign that something deeply important to you has been threatened or violated. **What is the deepest need that feels unmet right now** (e.g., need for fairness, respect, recognition)?",
                    "It sounds like a profound sense of frustration. Before reacting, try the **10-Second Rule** to allow the emotional wave to peak and subside. When you are ready, use the **Mindora Journal** to write a 'fury letter' that you absolutely will NOT send. Get it all out safely.",
                    "Anger is a valid messenger. What is the constructive action this anger is suggesting you take? Is it setting a boundary? Is it having a difficult conversation? Let's explore the *purpose* of the feeling.",
                    "I acknowledge your rage. When you feel ready, I suggest finding a safe, physical release—stomp your feet, clench a stress ball, or find a quick release video in the **Mindora video section**. After, tell me about the event that triggered this feeling."
            });
        }

        // --- NEW THERAPEUTIC CATEGORIES ---

        // Future Planning/Goal Setting (Therapeutic concept)
        if (message.matches(".*\\b(future|goals|planning|next steps|where to go|making changes|new year resolution)\\b.*")) {
            return getRandomResponse(new String[]{
                    "It sounds like you're ready to look ahead, which is a wonderful sign of forward momentum. Let's use the **S.M.A.R.T. goal framework** (Specific, Measurable, Achievable, Relevant, Time-bound). What's one small goal you can set for the next 7 days?",
                    "I hear your desire for change. Change is best approached with small steps. Use the **Mindora Journal** to create a **'Vision Board'** list of 3 things you want to feel one month from now. What's the smallest action you can take toward that vision today?",
                    "Setting intentions for the future is powerful. When planning, remember to build in 'buffer time' for setbacks and self-care. What's one change you're considering, and what potential challenges do you anticipate?",
            });
        }

        // Relationship Issues (Focus on Personal Agency)
        if (message.matches(".*\\b(relationship|boyfriend|girlfriend|partner|breakup|broke up|fight|argument|divorce|lonely in a crowd)\\b.*")) {
            return getRandomResponse(new String[]{
                    "I'm sorry you're navigating this difficulty. Relationship struggles are intensely painful. What is the one thing you can control in this dynamic? Hint: It's always your own actions and reactions. You can journal about your feelings privately in **Mindora**.",
                    "It sounds like you're feeling hurt or confused. Let's focus on **validation**. Your feelings are valid, even if the other person doesn't understand them. What do *you* need from yourself right now to cope with this pain?",
                    "I hear the conflict. When emotions are high, it's easy to lose perspective. What's one thing you truly appreciate about yourself, regardless of the status of this relationship? Re-anchoring to your own value is essential.",
                    "Breakups and conflict bring profound grief. If you need perspective, look for supportive posts in the **Mindora Community tab**—many others have navigated this pain. How are you taking care of your physical needs amidst this emotional chaos?",
            });
        }

        // Grief & Loss (Compassionate Holding)
        if (message.matches(".*\\b(grief|lost|passed away|death|dying|miss them|sad about loss|mourning|breakup grief|empty space)\\b.*")) {
            return getRandomResponse(new String[]{
                    "I am so very sorry for your loss. Grief is not a set of stages; it's a profound, personal process. There's no rush to feel 'better.' Can you offer yourself the same **kindness** you would offer a dearest friend who was going through this?",
                    "I hear the ache of that loss. Please know that the love remains, even when the presence is gone. Try using the **Mindora Journal** to write down one positive memory, no matter how small, to honor their impact.",
                    "The pain of loss is consuming. If you need gentle distraction and comfort, listen to a calming audio in the **Mindora app's designated section**. Remember to keep meeting your basic needs like hydration and a simple meal.",
            });
        }

        // Self-Care Check (Proactive Inquiry)
        if (message.matches(".*\\b(self-care|taking care of myself|rest|break|recharge|doing okay)\\b.*")) {
            return getRandomResponse(new String[]{
                    "That's a vital question. Let's check in: On a scale of 1 to 10 (10 being fully recharged), where are you? What is one **non-negotiable** act of self-care you can commit to today?",
                    "I'm glad you brought up self-care! It's not a luxury; it's essential. Which area needs the most attention: **Physical** (sleep, food, movement), **Emotional** (boundaries, processing), or **Spiritual** (purpose, connection)?",
                    "Excellent inquiry. If you're struggling to commit, let's explore **Micro-Self-Care**. Can you commit to 60 seconds of doing nothing, simply listening to the **Mindora Calming Audio**? Small moments add up.",
            });
        }

        // --- GENERAL REFLECTION & FALLBACK ---

        // General encouragement/Listening (Deep Fallback)
        if (conversationHistory.size() > 7) {
            return getRandomResponse(new String[]{
                    "I hear the weight of this struggle, and I want to reflect that back to you: **It sounds like you are feeling [Name the central emotion: exhausted, trapped, misunderstood].** Is that accurate?",
                    "Thank you for sharing such a personal experience. What are the **beliefs** you hold about yourself or this situation that might be making the problem feel bigger?",
                    "I'm listening closely. When you feel stuck, sometimes it helps to visualize. If this feeling were a cloud, what color is it, and is it moving? Use the **Mindora Journal** to draw or describe it.",
                    "That's a lot to carry. Remember, you don't have to solve everything today. What is the **most compassionate thing** you could say to yourself right now?",
                    "You've shown immense resilience just by engaging with me. For a boost, check the **Mindora Quotes section**. What support do you need most right now: **Validation, Problem-Solving, or Distraction?**",
            });
        }

        // Default responses (Deepened Reflective Responses)
        return getRandomResponse(new String[]{
                "I'm here to listen. Can you tell me more about what that feeling is *like*? Describe it in detail so I can understand its texture.",
                "Thank you for sharing that. **What's the meaning you're making** of this situation? Our interpretations often dictate our emotional response.",
                "I hear you. If you could give this challenge a name, what would it be? Sometimes naming the struggle gives you power over it.",
                "That sounds challenging. If you need a moment to gather your thoughts, please take it. You can write your key feelings in the **Mindora Journal** and share them when ready.",
                "I'm here for you. What is one **small, gentle comfort** you can offer yourself in the next five minutes?",
                "What is the **old story** you are telling yourself right now? And what is one **new, kinder story** you could start telling?",
        });
    }

    private String getRandomResponse(String[] responses) {
        return responses[random.nextInt(responses.length)];
    }

    public void clearHistory() {
        conversationHistory.clear();
        System.out.println(TAG + ": Conversation history cleared.");
    }
}