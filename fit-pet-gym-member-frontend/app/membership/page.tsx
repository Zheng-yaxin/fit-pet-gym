"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import {
  ArrowLeft,
  CreditCard,
  Crown,
  Dumbbell,
  MapPinned,
  ShieldCheck,
  Sparkles,
  Wallet,
  Zap
} from "lucide-react";
import { FeatureMotionDirector } from "@/components/motion/feature-motion-director";
import { FeatureStatusCard } from "@/components/motion/feature-status-card";
import {
  buyMemberCard,
  getMemberBenefitSummary,
  getMemberProfile,
  rechargeWallet,
  type MemberBenefitSummary,
  type MemberProfile
} from "@/lib/member-api";
import "../feature-placeholder.css";

type CardPlan = {
  key: string;
  name: string;
  duration: number;
  label: string;
  price: number;
  icon: typeof Zap;
  color: string;
  features: string[];
};

const PLANS: CardPlan[] = [
  { key: "日卡", name: "日卡体验", duration: 1, label: "1 天", price: 29, icon: Zap, color: "#f59e0b", features: ["当天自由入场", "基础器材使用", "团课体验 1 次"] },
  { key: "周卡", name: "周卡畅练", duration: 7, label: "7 天", price: 99, icon: Sparkles, color: "#8b5cf6", features: ["7 天入场", "全部器材使用", "团课预约", "私教体验 1 次"] },
  { key: "月卡", name: "月卡塑形", duration: 30, label: "30 天", price: 299, icon: CreditCard, color: "#3b82f6", features: ["30 天入场", "团课预约", "私教课程 2 节", "月度体测报告"] },
  { key: "年卡", name: "年卡尊享", duration: 365, label: "365 天", price: 1999, icon: Crown, color: "#ef4444", features: ["全年入场", "器材优先使用", "私教课程 12 节", "专属储物柜", "月度体测报告"] }
];

function money(value?: number) {
  return `¥${Number(value ?? 0).toFixed(2)}`;
}

export default function MembershipPage() {
  const [profile, setProfile] = useState<MemberProfile | null>(null);
  const [benefits, setBenefits] = useState<MemberBenefitSummary | null>(null);
  const [selectedPlan, setSelectedPlan] = useState<CardPlan | null>(null);
  const [rechargeAmount, setRechargeAmount] = useState("100");
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const nextProfile = await getMemberProfile();
      setProfile(nextProfile);
      if (nextProfile?.id) {
        setBenefits(await getMemberBenefitSummary(nextProfile.id));
      } else {
        setBenefits(null);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "会员权益数据加载失败。");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const walletBalance = Number(benefits?.walletBalance ?? profile?.balance ?? 0);
  const currentCard = benefits?.card;
  const selectedAffordable = selectedPlan ? walletBalance >= selectedPlan.price : false;

  const headline = useMemo(() => {
    if (!benefits?.active) return "还没有激活会员卡";
    if ((benefits.daysLeft ?? 0) <= 7) return "会员卡即将到期";
    return `${currentCard?.cardType ?? "会员卡"}权益已绑定场馆资产`;
  }, [benefits?.active, benefits?.daysLeft, currentCard?.cardType]);

  const handleBuyCard = async () => {
    if (!selectedPlan || !profile?.id) return;
    setBusy(true);
    setError("");
    setSuccess("");
    try {
      await buyMemberCard({
        memberId: profile.id,
        cardType: selectedPlan.key,
        amount: selectedPlan.price,
        duration: selectedPlan.duration,
        remark: `会员端购买${selectedPlan.name}`
      });
      setSuccess(`已购买${selectedPlan.name}，权益会立即刷新。`);
      setSelectedPlan(null);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "购买失败，请确认钱包余额和会员状态。");
    } finally {
      setBusy(false);
    }
  };

  const handleRecharge = async () => {
    if (!profile?.id) return;
    const amount = Number(rechargeAmount);
    if (!Number.isFinite(amount) || amount <= 0) {
      setError("充值金额必须大于 0。");
      return;
    }
    setBusy(true);
    setError("");
    setSuccess("");
    try {
      await rechargeWallet(profile.id, amount, "会员端权益页充值");
      setSuccess(`已充值 ${money(amount)}。`);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "充值失败，请稍后再试。");
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="feature-page feature-motion-page feature-motion-membership" aria-label="会员权益">
      <FeatureMotionDirector variant="membership" />
      <div className="feature-shell wide">
        <Link className="feature-back" href="/profile"><ArrowLeft size={18} />返回个人中心</Link>

        <section className="feature-panel">
          <div className="feature-heading">
            <span>Membership</span>
            <h1>会员权益中心</h1>
            <p>会员卡不只是购买记录，这里会把入场、私教、团课、器材和实时场区建议绑定到当前账户。</p>
          </div>

          {error ? <FeatureStatusCard kind="error" title="会员权益暂时没接上" detail={error} /> : null}
          {success ? <FeatureStatusCard kind="success" title="会员操作已完成" detail={success} /> : null}
          {loading ? <FeatureStatusCard title="权益正在核验" detail="正在同步会员卡、钱包、器材和场馆热力。" /> : null}

          <div className="feature-grid three">
            <article className="feature-data">
              <span><ShieldCheck size={18} />当前状态</span>
              <h2>{headline}</h2>
              <p>{currentCard?.expireDate ? `有效期至 ${currentCard.expireDate.slice(0, 10)}，剩余 ${benefits?.daysLeft ?? 0} 天` : "购买会员卡后会解锁入场和器材权益。"}</p>
            </article>
            <article className="feature-data">
              <span><Wallet size={18} />钱包余额</span>
              <h2>{money(walletBalance)}</h2>
              <p>{profile?.nickname || profile?.name || benefits?.memberName || "当前会员"}</p>
            </article>
            <article className="feature-data">
              <span><Crown size={18} />核心权益</span>
              <h2>{benefits?.privateTrainingQuota ?? 0} 节私教</h2>
              <p>{benefits?.unlimitedEntry ? "入场不限次数" : "按剩余次数入场"}{benefits?.lockerAccess ? "，含储物柜权益" : ""}</p>
            </article>
          </div>

          <div className="feature-grid two" style={{ marginTop: 16 }}>
            <article className="feature-list">
              <span><ShieldCheck size={18} />权益清单</span>
              {(benefits?.entitlements?.length ? benefits.entitlements : ["暂无激活权益，先选择一个会员计划。"]).map((item) => <p key={item}>{item}</p>)}
              {(benefits?.actions ?? []).map((item) => <p key={item} style={{ color: "var(--accent)" }}>{item}</p>)}
            </article>
            <article className="feature-list">
              <span><MapPinned size={18} />推荐场区</span>
              {(benefits?.recommendedAreas ?? []).slice(0, 4).map((area) => (
                <p key={area.areaId}>
                  {area.areaName ?? "场区"}：{area.occupancyPercent ?? 0}% 占用，{area.action ?? "适合下一组训练"}
                </p>
              ))}
              {!benefits?.recommendedAreas?.length ? <p>暂无实时人流快照。</p> : null}
            </article>
          </div>

          <div className="feature-heading" style={{ marginTop: 18 }}>
            <span>Venue Assets</span>
            <h2>可用器材绑定</h2>
          </div>
          <div className="feature-grid four" style={{ marginTop: 8 }}>
            {(benefits?.availableAssets ?? []).slice(0, 8).map((asset) => (
              <article className="feature-data inner" key={asset.equipmentId}>
                <span><Dumbbell size={16} />{asset.categoryName ?? "器材"}</span>
                <h2 style={{ fontSize: 20 }}>{asset.name ?? "可用器材"}</h2>
                <p>{asset.location ?? "前台确认位置"} · {asset.statusDesc ?? "可用"}</p>
              </article>
            ))}
          </div>

          <div className="feature-heading" style={{ marginTop: 18 }}>
            <span>Select Plan</span>
            <h2>选择或续费计划</h2>
          </div>

          <div className="feature-grid two" style={{ marginTop: 8 }}>
            {PLANS.map((plan) => {
              const Icon = plan.icon;
              const isSelected = selectedPlan?.key === plan.key;
              return (
                <article
                  key={plan.key}
                  className="feature-list"
                  style={{ border: isSelected ? `2px solid ${plan.color}` : undefined, cursor: "pointer" }}
                  onClick={() => setSelectedPlan(plan)}
                >
                  <span style={{ color: plan.color }}><Icon size={20} />{plan.name}</span>
                  <div style={{ textAlign: "center", padding: "12px 0" }}>
                    <h2 style={{ fontSize: 36, color: plan.color, margin: 0 }}>{money(plan.price)}</h2>
                    <p style={{ color: "var(--muted)" }}>{plan.label}</p>
                  </div>
                  {plan.features.map((feature) => <p key={feature}>✓ {feature}</p>)}
                </article>
              );
            })}
          </div>

          {selectedPlan ? (
            <div style={{ textAlign: "center", marginTop: 16 }}>
              <button
                type="button"
                disabled={busy}
                onClick={handleBuyCard}
                style={{
                  background: selectedPlan.color,
                  color: "#fff",
                  border: "none",
                  padding: "12px 48px",
                  borderRadius: 12,
                  fontSize: 16,
                  fontWeight: 700,
                  cursor: busy ? "not-allowed" : "pointer"
                }}
              >
                {busy ? "处理中..." : `购买 ${selectedPlan.name} · ${money(selectedPlan.price)}`}
              </button>
              <p style={{ color: selectedAffordable ? "var(--accent)" : "#dc2626", marginTop: 8, fontSize: 13 }}>
                当前余额 {money(walletBalance)}，{selectedAffordable ? "余额充足" : "余额可能不足，请先充值"}
              </p>
            </div>
          ) : null}

          <div style={{ marginTop: 24, textAlign: "center" }}>
            <div style={{ display: "inline-flex", gap: 8, alignItems: "center", background: "var(--surface)", padding: "8px 16px", borderRadius: 10 }}>
              <span style={{ fontSize: 14 }}>快速充值</span>
              <input
                value={rechargeAmount}
                inputMode="decimal"
                onChange={(event) => setRechargeAmount(event.target.value)}
                style={{ width: 86, padding: "4px 8px", borderRadius: 6, border: "1px solid var(--border)", background: "var(--bg)", textAlign: "center" }}
              />
              <button type="button" disabled={busy} onClick={handleRecharge} style={{ background: "var(--accent)", color: "#fff", border: "none", padding: "4px 16px", borderRadius: 6, cursor: "pointer" }}>
                充值
              </button>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}
