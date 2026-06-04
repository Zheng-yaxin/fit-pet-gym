"use client";

import Link from "next/link";
import { FormEvent, Suspense, useEffect, useMemo, useState } from "react";
import { useSearchParams } from "next/navigation";
import { ArrowLeft, MessageCircle, Send } from "lucide-react";
import { FeatureMotionDirector } from "@/components/motion/feature-motion-director";
import { FeatureStatusCard } from "@/components/motion/feature-status-card";
import {
  getChatContacts,
  getChatMessages,
  sendChatMessage,
  type ChatContact,
  type ChatMessage
} from "@/lib/member-api";
import "../feature-placeholder.css";

function contactKey(contact: Pick<ChatContact, "id" | "role">) {
  return `${contact.role ?? "COACH"}-${contact.id}`;
}

function ChatPageContent() {
  const searchParams = useSearchParams();
  const [contacts, setContacts] = useState<ChatContact[]>([]);
  const [activeContact, setActiveContact] = useState<ChatContact | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [draft, setDraft] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");

  const targetContact = useMemo<ChatContact | null>(() => {
    const targetId = Number(searchParams.get("targetId"));
    if (!targetId) return null;
    return {
      id: targetId,
      role: searchParams.get("role") ?? "COACH",
      name: searchParams.get("targetName") ?? "Coach follow-up"
    };
  }, [searchParams]);

  useEffect(() => {
    const loadContacts = async () => {
      setLoading(true);
      setError("");
      try {
        const nextContacts = (await getChatContacts()) ?? [];
        const requested = targetContact
          ? nextContacts.find((contact) => contactKey(contact) === contactKey(targetContact)) ?? targetContact
          : null;
        setContacts(requested && !nextContacts.some((contact) => contactKey(contact) === contactKey(requested))
          ? [requested, ...nextContacts]
          : nextContacts);
        setActiveContact(requested ?? nextContacts[0] ?? null);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Chat contacts failed to load.");
      } finally {
        setLoading(false);
      }
    };
    loadContacts();
  }, [targetContact]);

  useEffect(() => {
    const loadMessages = async () => {
      if (!activeContact?.id) {
        setMessages([]);
        return;
      }
      setError("");
      try {
        setMessages(await getChatMessages(activeContact.id, activeContact.role ?? "COACH"));
      } catch (err) {
        setError(err instanceof Error ? err.message : "Chat history failed to load.");
      }
    };
    loadMessages();
  }, [activeContact]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const content = draft.trim();
    if (!activeContact?.id || !content) return;
    setSending(true);
    setError("");
    try {
      const sent = await sendChatMessage({
        receiverId: activeContact.id,
        receiverRole: activeContact.role ?? "COACH",
        content
      });
      setMessages((current) => [...current, sent]);
      setDraft("");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Message send failed.");
    } finally {
      setSending(false);
    }
  };

  return (
    <main className="feature-page feature-motion-page feature-motion-booking" aria-label="coach chat">
      <FeatureMotionDirector variant="booking" />
      <div className="feature-shell wide">
        <Link className="feature-back" href="/courses">
          <ArrowLeft size={18} />
          Back to courses
        </Link>
        <section className="feature-panel">
          <div className="feature-heading">
            <span>Coach chat</span>
            <h1>Follow-up desk</h1>
            <p>Feedback follow-ups and personal-training contacts share the same backend chat thread.</p>
          </div>

          {error ? <FeatureStatusCard kind="error" title="Chat temporarily paused" detail={error} /> : null}
          {loading ? <FeatureStatusCard title="Finding coach contacts" detail="Loading bookings and feedback follow-up threads." /> : null}

          <div className="feature-grid two">
            <article className="feature-list">
              <span>
                <MessageCircle size={16} />
                Contacts
              </span>
              {contacts.map((contact) => (
                <div className="feature-row" key={contactKey(contact)}>
                  <div>
                    <h3>{contact.name ?? `Coach #${contact.id}`}</h3>
                    <p>{contact.role ?? "COACH"}{contact.unreadCount ? ` / ${contact.unreadCount} unread` : ""}</p>
                  </div>
                  <button type="button" onClick={() => setActiveContact(contact)}>
                    {activeContact && contactKey(activeContact) === contactKey(contact) ? "Open" : "Select"}
                  </button>
                </div>
              ))}
              {!contacts.length && !loading ? <p>No coach chat is available yet.</p> : null}
            </article>

            <article className="feature-list">
              <span>Thread</span>
              <h2>{activeContact?.name ?? "Select a coach"}</h2>
              <div className="feature-chat-thread">
                {messages.map((message) => (
                  <div className={message.isSelf ? "feature-chat-bubble self" : "feature-chat-bubble"} key={message.id ?? `${message.createTime}-${message.content}`}>
                    <strong>{message.isSelf ? "You" : message.senderName ?? activeContact?.name ?? "Coach"}</strong>
                    <p>{message.content}</p>
                    <small>{message.createTime ? new Date(message.createTime).toLocaleString() : ""}</small>
                  </div>
                ))}
                {!messages.length ? <p className="feature-muted">No messages yet. Send the first follow-up note.</p> : null}
              </div>
              <form className="feature-inline feature-chat-compose" onSubmit={handleSubmit}>
                <input
                  value={draft}
                  onChange={(event) => setDraft(event.target.value)}
                  placeholder="Tell the coach what you want to adjust..."
                  disabled={!activeContact || sending}
                />
                <button type="submit" disabled={!activeContact || sending || !draft.trim()}>
                  <Send size={16} />
                  Send
                </button>
              </form>
            </article>
          </div>
        </section>
      </div>
    </main>
  );
}

export default function ChatPage() {
  return (
    <Suspense fallback={<main className="feature-page" aria-label="coach chat loading" />}>
      <ChatPageContent />
    </Suspense>
  );
}
