# Fit-Pet Health Homepage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first usable Fit-Pet member homepage: a warm Q-version health companion UI driven by real backend data, with first-login character onboarding and a profile hub for member assets.

**Architecture:** Keep the current `fit-pet-gym-member-frontend` Next.js app. Split the oversized homepage into focused client components, centralize API access and health-derived view models, persist onboarding state locally for V1, and reuse existing backend endpoints for gender, body data, diet gap, training logs, courses, traffic, wallet, cards, repair, and chat entry points.

**Tech Stack:** Next.js 16, React 19, TypeScript, Framer Motion, lucide-react, CSS modules/global route CSS, existing Spring Boot REST API.

---

## Design Read

Reading this as a product UI for gym members, with a warm Q-version health companion language. The interface should feel like a practical daily health cockpit wrapped in soft hand-drawn game charm. It should not feel like a loud mobile game, a generic SaaS dashboard, or a copied version of the reference screenshot.

Design dials:

- Visual density: 5/10, balanced daily app density.
- Layout variance: 5/10, predictable product layout with a few playful asymmetries.
- Motion intensity: 6/10, one cinematic onboarding moment plus restrained state motion.
- Register: product UI. Design serves daily health decisions.

## V1 Scope

In scope:

- First-login onboarding after registration or login.
- Gender choice as boy or girl, both using the same app structure.
- Height, weight, age or birth date setup through existing health data.
- Optional diet target setup if backend target data is missing.
- Homepage with real data slots for:
  - Today's calorie gap.
  - Today's exercise duration.
  - Latest height, weight, age.
  - Training plan.
  - Diet record and AI recognition.
  - Course booking, including group class and personal training.
  - Body record.
  - Venue status.
- Character state derived from calorie gap and exercise duration.
- Q-version profile avatar entry to personal homepage.
- Personal homepage entries for member profile, member card, wallet, equipment repair, chat/messages, settings.
- UTF-8 Chinese copy cleanup for touched member frontend files.
- Loading, empty, error, and reduced-motion states.

Out of scope for V1:

- New backend game profile tables.
- Public leaderboards, gacha, pet evolution, inventory economy.
- Real device synchronization.
- Full chat UI rebuild.
- Full equipment repair workflow redesign.
- Generated bitmap final character asset pack unless the coded SVG direction is insufficient.

## Backend Entry Map

Use existing endpoints first:

- `POST /auth/register`, already accepts `gender`.
- `GET /member/profile`
- `GET /health/data/latest`
- `POST /health/data`
- `GET /health/diet/gap?date=YYYY-MM-DD`
- `GET /health/diet/summary?date=YYYY-MM-DD`
- `GET /health/diet/target`
- `PUT /health/diet/target`
- `GET /training/logs`
- `GET /training/plans/current`
- `POST /training/checkin/start`
- `POST /training/checkin/end`
- `POST /training/logs`
- `GET /member/course/list`
- `GET /member/course/schedules`
- `GET /member/course/my-enrollments`
- `GET /member/coach/list`
- `GET /member/personal-training/bookings`
- `GET /gym/traffic/current`
- `GET /gym/traffic/heatmap`
- `GET /member/card/valid/{memberId}`
- `GET /member/wallet/balance/{memberId}`
- `GET /equipment/repair/my`
- `GET /chat/unread-count`

V1 should not require backend schema changes. Store character choice and onboarding completion in local storage unless a later task adds a backend game profile endpoint.

## File Structure

Create:

- `fit-pet-gym-member-frontend/DESIGN.md`: project-level visual system and anti-patterns.
- `fit-pet-gym-member-frontend/lib/api-client.ts`: typed fetch wrapper with token support and normalized errors.
- `fit-pet-gym-member-frontend/lib/member-api.ts`: member, health, training, course, traffic, profile hub API functions.
- `fit-pet-gym-member-frontend/lib/home-model.ts`: pure data normalization, age calculation, today summaries, character mood logic.
- `fit-pet-gym-member-frontend/lib/onboarding-store.ts`: local storage helpers for first-login onboarding.
- `fit-pet-gym-member-frontend/components/home/character-stage.tsx`: boy/girl character rendering and motion states.
- `fit-pet-gym-member-frontend/components/home/daily-focus.tsx`: calorie gap and exercise duration display.
- `fit-pet-gym-member-frontend/components/home/body-stats-row.tsx`: height, weight, age under the character.
- `fit-pet-gym-member-frontend/components/home/home-action-grid.tsx`: five homepage feature entries.
- `fit-pet-gym-member-frontend/components/home/profile-avatar-button.tsx`: Q-version avatar link to profile hub.
- `fit-pet-gym-member-frontend/components/home/home-skeleton.tsx`: layout-matched loading state.
- `fit-pet-gym-member-frontend/components/onboarding/onboarding-flow.tsx`: first-login setup flow.
- `fit-pet-gym-member-frontend/components/onboarding/character-choice.tsx`: boy/girl selection.
- `fit-pet-gym-member-frontend/components/onboarding/body-data-form.tsx`: health data form.
- `fit-pet-gym-member-frontend/components/onboarding/target-setup-form.tsx`: optional calorie target form.
- `fit-pet-gym-member-frontend/components/onboarding/arrival-animation.tsx`: restrained cinematic entry.
- `fit-pet-gym-member-frontend/app/profile/page.tsx`: personal homepage route.
- `fit-pet-gym-member-frontend/app/profile/profile.css`: personal homepage styling.
- `fit-pet-gym-member-frontend/app/onboarding/page.tsx`: onboarding route.
- `fit-pet-gym-member-frontend/app/onboarding/onboarding.css`: onboarding styling.

Modify:

- `fit-pet-gym-member-frontend/app/page.tsx`: replace current demo dashboard with real homepage shell.
- `fit-pet-gym-member-frontend/app/page.css`: replace page-specific dashboard layout styles.
- `fit-pet-gym-member-frontend/app/globals.css`: add clean tokens, motion variables, responsive tokens, and font stack.
- `fit-pet-gym-member-frontend/app/layout.tsx`: fix metadata copy and optionally add app-level font setup.
- `fit-pet-gym-member-frontend/package.json`: add test scripts only if test tooling is introduced.

Optional, only if coded SVG characters do not reach the desired quality:

- `fit-pet-gym-member-frontend/public/characters/girl-home.png`
- `fit-pet-gym-member-frontend/public/characters/boy-home.png`
- `fit-pet-gym-member-frontend/public/characters/girl-home@2x.png`
- `fit-pet-gym-member-frontend/public/characters/boy-home@2x.png`

## Design System Decisions

Atmosphere:

- Warm cream grid-paper canvas, but cleaner than the reference screenshot.
- Thick ink outline for primary illustrated elements.
- Soft green, warm yellow, powder blue, and coral as functional accents.
- Cards are used for distinct actionable modules only. Do not nest cards.
- The homepage is not a menu wall. It is a daily status scene with clear next actions.

Color roles:

- Canvas Oat: `#F8F1E0`, primary background.
- Canvas Toast: `#EFE1C6`, background depth.
- Surface Milk: `#FFF9EC`, main panels.
- Ink Charcoal: `#20242D`, text and illustration outline, never pure black.
- Muted Olive Gray: `#686B61`, secondary text.
- Vital Mint: `#74DFA8`, success, within-target, active health state.
- Motion Blue: `#77C5E8`, training and movement.
- Warm Yolk: `#F6C84C`, highlight and achievement.
- Soft Coral: `#F48276`, attention and calorie warning.
- Petal Pink: `#F59BBC`, girl character accent.

Typography:

- Use the existing Chinese system stack first for reliable local rendering.
- Avoid display fonts for data labels.
- Keep labels compact and readable.
- Body copy should stay direct and short.

Motion:

- Onboarding gets the one cinematic moment.
- Homepage motion should be stateful: breathing, waiting, stretching, training, replenishing.
- Feedback duration should stay in the 150 to 350 ms range.
- Character idle loops can be slower, around 2400 to 4200 ms.
- Animate `transform` and `opacity`. Avoid layout-property animation.
- Respect `prefers-reduced-motion`.

Anti-patterns:

- No neon glow.
- No purple-blue tech gradient.
- No loud game explosion effects.
- No fake metrics.
- No three-equal-card feature wall as the homepage.
- No generic "AI made this" copy.
- No emojis as icons.
- No overlapping text or controls.

## Character State Rules

Input:

```ts
type CharacterGender = "boy" | "girl";

type DailyHealthSignals = {
  caloriesGap: number | null;
  exerciseMinutes: number;
  exerciseTargetMinutes: number;
};

type CharacterMood =
  | "idle"
  | "needsFuel"
  | "warmup"
  | "steady"
  | "training"
  | "celebrate";
```

Rules:

- `celebrate`: `caloriesGap` exists, `Math.abs(caloriesGap) <= 150`, and `exerciseMinutes >= exerciseTargetMinutes`.
- `needsFuel`: `caloriesGap` exists and `caloriesGap > 450`.
- `warmup`: `exerciseMinutes < Math.max(10, exerciseTargetMinutes * 0.35)`.
- `training`: `exerciseMinutes >= exerciseTargetMinutes`.
- `steady`: `caloriesGap` exists and `Math.abs(caloriesGap) <= 250`.
- `idle`: fallback when data is missing.

Tone:

- `needsFuel` copy should be gentle: "今天能量还差一点，补一餐更稳。"
- `warmup` copy should be inviting: "先热身 10 分钟，身体会慢慢醒来。"
- `celebrate` copy should be calm: "今天节奏很好，继续保持。"

## Task 1: Write The Design System

**Files:**

- Create: `fit-pet-gym-member-frontend/DESIGN.md`

- [ ] **Step 1: Create the design system document**

Write `DESIGN.md` with these sections:

```md
# Fit-Pet Member Frontend Design System

## Visual Theme

Fit-Pet is a warm daily health companion. The visual language is Q-version, hand-drawn, and functional. It should feel like a clean health app that happens to have a lovable character, not like a loud mobile game.

## Palette

- Canvas Oat `#F8F1E0`: page background.
- Canvas Toast `#EFE1C6`: background depth.
- Surface Milk `#FFF9EC`: large panels.
- Ink Charcoal `#20242D`: primary text and outline.
- Muted Olive Gray `#686B61`: secondary text.
- Vital Mint `#74DFA8`: success and healthy range.
- Motion Blue `#77C5E8`: training and movement.
- Warm Yolk `#F6C84C`: highlights.
- Soft Coral `#F48276`: attention states.
- Petal Pink `#F59BBC`: girl character accent.

## Typography

Use the Chinese system stack: `"Alibaba PuHuiTi", "HarmonyOS Sans SC", "Microsoft YaHei UI", "Segoe UI", sans-serif`. Use weight, spacing, and contrast for hierarchy. Do not use decorative display fonts for labels, buttons, or data.

## Components

Buttons are thick-outline, tactile, and at least 44px tall. Cards are reserved for distinct actions. Do not nest cards. Form labels sit above inputs. Loading states use skeletons that match the layout.

## Motion

Motion explains state. The onboarding arrival animation is the only cinematic sequence. Homepage motion is gentle breathing, stretching, training, or replenishing. Animate transforms and opacity only. Always support reduced motion.

## Banned

No neon glow, no purple-blue tech gradient, no fake metrics, no three-equal-card menu wall, no emojis as icons, no pure black, no overlapping elements, no generic AI copy.
```

- [ ] **Step 2: Review for implementation usefulness**

Confirm every color has a role, every motion rule names where it applies, and every banned item maps to this project.

- [ ] **Step 3: Commit**

```bash
git add fit-pet-gym-member-frontend/DESIGN.md
git commit -m "docs: add fit-pet member design system"
```

## Task 2: Build The API And Home Model Layer

**Files:**

- Create: `fit-pet-gym-member-frontend/lib/api-client.ts`
- Create: `fit-pet-gym-member-frontend/lib/member-api.ts`
- Create: `fit-pet-gym-member-frontend/lib/home-model.ts`

- [ ] **Step 1: Create typed API client**

Implement `lib/api-client.ts`:

```ts
export type ApiResult<T> = {
  code?: number;
  msg?: string;
  message?: string;
  data?: T;
};

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

export async function apiRequest<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = typeof window !== "undefined" ? window.localStorage.getItem("token") : null;
  const response = await fetch(`/api${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init.headers ?? {})
    }
  });

  if (!response.ok) {
    throw new ApiError(`请求失败：${response.status}`, response.status);
  }

  const result = (await response.json()) as ApiResult<T>;
  if (typeof result.code === "number" && result.code !== 200 && result.code !== 0) {
    throw new ApiError(result.msg ?? result.message ?? "请求失败", response.status);
  }

  return result.data as T;
}
```

- [ ] **Step 2: Create member API functions**

Implement `lib/member-api.ts`:

```ts
import { apiRequest } from "./api-client";

export type HealthData = {
  id?: number;
  userId?: number;
  gender?: 0 | 1;
  birthDate?: string;
  height?: number;
  weight?: number;
  bmi?: number;
  bodyFatRate?: number;
  measureTime?: string;
};

export type DietGap = {
  caloriesTarget?: number;
  caloriesActual?: number;
  caloriesGap?: number;
  proteinTarget?: number;
  proteinActual?: number;
  proteinGap?: number;
  fatTarget?: number;
  fatActual?: number;
  fatGap?: number;
  carbohydrateTarget?: number;
  carbohydrateActual?: number;
  carbohydrateGap?: number;
};

export type DietTarget = {
  caloriesTarget?: number;
  proteinTarget?: number;
  fatTarget?: number;
  carbohydrateTarget?: number;
};

export type TrainingLog = {
  id?: number;
  memberId?: number;
  planId?: number;
  trainingDate?: string;
  durationMinutes?: number;
  intensity?: number;
  caloriesBurned?: number;
  feeling?: string;
  remark?: string;
};

export type TrainingPlan = {
  id?: number;
  name?: string;
  goal?: string;
  status?: string;
};

export type TrafficSnapshot = {
  id?: number;
  areaName?: string;
  currentCount?: number;
  capacity?: number;
  snapshotTime?: string;
};

export function getLatestHealthData() {
  return apiRequest<HealthData | null>("/health/data/latest");
}

export function saveHealthData(data: HealthData) {
  return apiRequest<void>("/health/data", {
    method: "POST",
    body: JSON.stringify(data)
  });
}

export function getDietGap(date: string) {
  return apiRequest<DietGap>(`/health/diet/gap?date=${encodeURIComponent(date)}`);
}

export function getDietTarget() {
  return apiRequest<DietTarget | null>("/health/diet/target");
}

export function saveDietTarget(target: DietTarget) {
  return apiRequest<void>("/health/diet/target", {
    method: "PUT",
    body: JSON.stringify(target)
  });
}

export function getTrainingLogs() {
  return apiRequest<TrainingLog[]>("/training/logs");
}

export function getCurrentTrainingPlan() {
  return apiRequest<TrainingPlan | null>("/training/plans/current");
}

export function getCurrentTraffic() {
  return apiRequest<TrafficSnapshot[]>("/gym/traffic/current");
}
```

- [ ] **Step 3: Create pure home model functions**

Implement `lib/home-model.ts`:

```ts
import type { DietGap, HealthData, TrainingLog } from "./member-api";

export type CharacterGender = "boy" | "girl";
export type CharacterMood = "idle" | "needsFuel" | "warmup" | "steady" | "training" | "celebrate";

export type DailySummary = {
  caloriesGap: number | null;
  exerciseMinutes: number;
  exerciseTargetMinutes: number;
  caloriesBurned: number;
  mood: CharacterMood;
};

export function todayDateString(now = new Date()) {
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function calculateAge(birthDate?: string | null, now = new Date()) {
  if (!birthDate) return null;
  const birth = new Date(birthDate);
  if (Number.isNaN(birth.getTime())) return null;
  let age = now.getFullYear() - birth.getFullYear();
  const monthDelta = now.getMonth() - birth.getMonth();
  if (monthDelta < 0 || (monthDelta === 0 && now.getDate() < birth.getDate())) {
    age -= 1;
  }
  return age >= 0 ? age : null;
}

export function isSameLocalDate(value?: string, date = todayDateString()) {
  return Boolean(value && value.slice(0, 10) === date);
}

export function summarizeTrainingToday(logs: TrainingLog[], date = todayDateString()) {
  return logs.filter((log) => isSameLocalDate(log.trainingDate, date)).reduce(
    (summary, log) => ({
      exerciseMinutes: summary.exerciseMinutes + (log.durationMinutes ?? 0),
      caloriesBurned: summary.caloriesBurned + (log.caloriesBurned ?? 0)
    }),
    { exerciseMinutes: 0, caloriesBurned: 0 }
  );
}

export function resolveCharacterMood(
  caloriesGap: number | null,
  exerciseMinutes: number,
  exerciseTargetMinutes = 30
): CharacterMood {
  if (caloriesGap !== null && Math.abs(caloriesGap) <= 150 && exerciseMinutes >= exerciseTargetMinutes) {
    return "celebrate";
  }
  if (caloriesGap !== null && caloriesGap > 450) return "needsFuel";
  if (exerciseMinutes < Math.max(10, exerciseTargetMinutes * 0.35)) return "warmup";
  if (exerciseMinutes >= exerciseTargetMinutes) return "training";
  if (caloriesGap !== null && Math.abs(caloriesGap) <= 250) return "steady";
  return "idle";
}

export function buildDailySummary(dietGap: DietGap | null, logs: TrainingLog[], date = todayDateString()): DailySummary {
  const training = summarizeTrainingToday(logs, date);
  const caloriesGap = typeof dietGap?.caloriesGap === "number" ? dietGap.caloriesGap : null;
  return {
    caloriesGap,
    exerciseMinutes: training.exerciseMinutes,
    exerciseTargetMinutes: 30,
    caloriesBurned: training.caloriesBurned,
    mood: resolveCharacterMood(caloriesGap, training.exerciseMinutes, 30)
  };
}

export function healthDataComplete(data: HealthData | null | undefined) {
  return Boolean(data?.gender !== undefined && data?.birthDate && data?.height && data?.weight);
}
```

- [ ] **Step 4: Run type check**

Run:

```bash
npm run build
```

Expected: build may still fail because `app/page.tsx` has old demo code, but the new library files should not report TypeScript errors.

- [ ] **Step 5: Commit**

```bash
git add fit-pet-gym-member-frontend/lib/api-client.ts fit-pet-gym-member-frontend/lib/member-api.ts fit-pet-gym-member-frontend/lib/home-model.ts
git commit -m "feat: add member homepage data model"
```

## Task 3: Add Onboarding Storage And Flow

**Files:**

- Create: `fit-pet-gym-member-frontend/lib/onboarding-store.ts`
- Create: `fit-pet-gym-member-frontend/app/onboarding/page.tsx`
- Create: `fit-pet-gym-member-frontend/app/onboarding/onboarding.css`
- Create: `fit-pet-gym-member-frontend/components/onboarding/onboarding-flow.tsx`
- Create: `fit-pet-gym-member-frontend/components/onboarding/character-choice.tsx`
- Create: `fit-pet-gym-member-frontend/components/onboarding/body-data-form.tsx`
- Create: `fit-pet-gym-member-frontend/components/onboarding/target-setup-form.tsx`
- Create: `fit-pet-gym-member-frontend/components/onboarding/arrival-animation.tsx`

- [ ] **Step 1: Implement local onboarding store**

Create `lib/onboarding-store.ts`:

```ts
import type { CharacterGender } from "./home-model";

const ONBOARDING_KEY = "fitpet:onboarding:v1";
const CHARACTER_KEY = "fitpet:character:v1";

export type OnboardingState = {
  completed: boolean;
  characterGender: CharacterGender | null;
};

export function readOnboardingState(): OnboardingState {
  if (typeof window === "undefined") return { completed: false, characterGender: null };
  const completed = window.localStorage.getItem(ONBOARDING_KEY) === "complete";
  const characterGender = window.localStorage.getItem(CHARACTER_KEY) as CharacterGender | null;
  return {
    completed,
    characterGender: characterGender === "boy" || characterGender === "girl" ? characterGender : null
  };
}

export function saveCharacterGender(gender: CharacterGender) {
  window.localStorage.setItem(CHARACTER_KEY, gender);
}

export function completeOnboarding(gender: CharacterGender) {
  window.localStorage.setItem(CHARACTER_KEY, gender);
  window.localStorage.setItem(ONBOARDING_KEY, "complete");
}

export function resetOnboarding() {
  window.localStorage.removeItem(ONBOARDING_KEY);
  window.localStorage.removeItem(CHARACTER_KEY);
}
```

- [ ] **Step 2: Create onboarding route shell**

Create `app/onboarding/page.tsx`:

```tsx
import { OnboardingFlow } from "@/components/onboarding/onboarding-flow";
import "./onboarding.css";

export default function OnboardingPage() {
  return <OnboardingFlow />;
}
```

- [ ] **Step 3: Create flow component**

Implement `components/onboarding/onboarding-flow.tsx` as a client component with four steps:

1. Character choice.
2. Body data.
3. Target setup.
4. Arrival animation.

The flow should call:

- `saveCharacterGender(gender)` after choice.
- `saveHealthData({ gender: gender === "girl" ? 0 : 1, birthDate, height, weight, measureTime })` after body data.
- `saveDietTarget(target)` if target values are entered.
- `completeOnboarding(gender)` after arrival animation.
- `router.push("/")` after completion.

- [ ] **Step 4: Implement character choice**

Create two large tactile choices:

- Girl: pink headband, soft coral training accent.
- Boy: blue wristband, motion blue training accent.

Both choices should use the same copy pattern:

- Title: "选择你的健身伙伴"
- Description: "之后可以在个人主页调整。"

- [ ] **Step 5: Implement body data form**

Fields:

- Height in cm.
- Weight in kg.
- Birth date.

Validation:

- Height: 80 to 230.
- Weight: 25 to 250.
- Birth date: not future.

Error copy:

- "请输入合理的身高。"
- "请输入合理的体重。"
- "出生日期不能晚于今天。"

- [ ] **Step 6: Implement target setup**

Fields:

- Calorie target.
- Protein target.
- Fat target.
- Carbohydrate target.

Make this step skippable with copy: "先用系统推荐目标".

- [ ] **Step 7: Implement arrival animation**

Use Framer Motion:

- Ink outline draws in with opacity and scale.
- Character color fills in.
- Two main metrics preview slide into place.
- End with short breathing idle state.

Reduced motion behavior:

- Show final character instantly.
- Keep one fade transition under 150 ms.

- [ ] **Step 8: Commit**

```bash
git add fit-pet-gym-member-frontend/lib/onboarding-store.ts fit-pet-gym-member-frontend/app/onboarding fit-pet-gym-member-frontend/components/onboarding
git commit -m "feat: add first-login onboarding flow"
```

## Task 4: Rebuild Homepage Layout

**Files:**

- Modify: `fit-pet-gym-member-frontend/app/page.tsx`
- Modify: `fit-pet-gym-member-frontend/app/page.css`
- Create: `fit-pet-gym-member-frontend/components/home/character-stage.tsx`
- Create: `fit-pet-gym-member-frontend/components/home/daily-focus.tsx`
- Create: `fit-pet-gym-member-frontend/components/home/body-stats-row.tsx`
- Create: `fit-pet-gym-member-frontend/components/home/home-action-grid.tsx`
- Create: `fit-pet-gym-member-frontend/components/home/profile-avatar-button.tsx`
- Create: `fit-pet-gym-member-frontend/components/home/home-skeleton.tsx`

- [ ] **Step 1: Replace demo page state with data-driven state**

`app/page.tsx` should:

- Read onboarding state.
- Redirect to `/onboarding` if no completed onboarding and no latest health data.
- Fetch latest health data, diet gap, training logs, current training plan, and traffic.
- Build `DailySummary` using `buildDailySummary`.
- Render skeleton, error, or homepage.

- [ ] **Step 2: Define homepage composition**

Desktop layout:

- Top bar: logo left, today's date and profile avatar right.
- Left rail: `DailyFocus` with calorie gap and exercise duration.
- Center stage: character, mood copy, body stats row.
- Right action field: five primary entries.
- Lower strip: training plan preview and venue status preview.

Mobile layout:

- Top bar.
- Daily focus.
- Character stage.
- Body stats.
- Action grid.
- Plan and venue previews.

- [ ] **Step 3: Implement CharacterStage**

Props:

```ts
type CharacterStageProps = {
  gender: "boy" | "girl";
  mood: "idle" | "needsFuel" | "warmup" | "steady" | "training" | "celebrate";
  calorieGap: number | null;
  exerciseMinutes: number;
};
```

Behavior:

- Girl and boy share proportions and motion timing.
- Girl visual: headband, ponytail or bob variation, pink training outfit.
- Boy visual: short hair, blue training outfit.
- `needsFuel`: small bottle or snack prop enters.
- `warmup`: slow stretch loop.
- `training`: compact dumbbell or step motion.
- `celebrate`: short confident wave, no confetti burst.

- [ ] **Step 4: Implement DailyFocus**

Show:

- "今日热量缺口"
- `caloriesGap` value in kcal or "等待饮食记录"
- "今日运动时长"
- `exerciseMinutes / exerciseTargetMinutes`

Copy rules:

- Positive gap means still available target intake.
- Negative gap means exceeded target. Use "已超出 X kcal", not a shame phrase.

- [ ] **Step 5: Implement BodyStatsRow**

Show under character:

- Height.
- Weight.
- Age from `calculateAge`.

Each item is a round thick-outline control. Clicking can route to future body record page, but V1 can link to `/profile`.

- [ ] **Step 6: Implement HomeActionGrid**

Five entries:

- Training plan: route placeholder `/training`.
- Diet record and AI recognition: route placeholder `/health`.
- Course booking: route placeholder `/courses`.
- Body record: route placeholder `/body`.
- Venue status: route placeholder `/venue`.

If these routes do not exist yet, use buttons with disabled secondary state plus `aria-disabled` and clear "即将接入" copy only where necessary. Prefer adding simple placeholder routes later rather than dead links.

- [ ] **Step 7: Implement ProfileAvatarButton**

Profile avatar:

- Q-version face matching selected gender.
- Small unread indicator if chat unread count is later available.
- Route to `/profile`.

- [ ] **Step 8: Commit**

```bash
git add fit-pet-gym-member-frontend/app/page.tsx fit-pet-gym-member-frontend/app/page.css fit-pet-gym-member-frontend/components/home
git commit -m "feat: rebuild member health homepage"
```

## Task 5: Add Personal Homepage

**Files:**

- Create: `fit-pet-gym-member-frontend/app/profile/page.tsx`
- Create: `fit-pet-gym-member-frontend/app/profile/profile.css`

- [ ] **Step 1: Create profile route**

Sections:

- Member profile summary.
- Member card.
- Wallet.
- Equipment repair.
- Chat and messages.
- Settings and character.

- [ ] **Step 2: Wire route entries**

Use real backend entry labels:

- Member card: `/member/card/valid/{memberId}` when member id is known.
- Wallet: `/member/wallet/balance/{memberId}` when member id is known.
- Equipment repair: `/equipment/repair/my`.
- Chat/messages: `/chat/unread-count`.

V1 can show loading and fallback states if member id is unavailable from `/member/profile`.

- [ ] **Step 3: Keep profile visual hierarchy quieter than homepage**

The profile route should feel like a clean hub, not a second game dashboard. Use dividers and compact tiles where possible.

- [ ] **Step 4: Commit**

```bash
git add fit-pet-gym-member-frontend/app/profile
git commit -m "feat: add member profile hub"
```

## Task 6: Add Placeholder Routes Or Safe Navigation

**Files:**

- Optional create: `fit-pet-gym-member-frontend/app/training/page.tsx`
- Optional create: `fit-pet-gym-member-frontend/app/health/page.tsx`
- Optional create: `fit-pet-gym-member-frontend/app/courses/page.tsx`
- Optional create: `fit-pet-gym-member-frontend/app/body/page.tsx`
- Optional create: `fit-pet-gym-member-frontend/app/venue/page.tsx`

- [ ] **Step 1: Decide route strategy**

If routes do not already exist, create minimal placeholder pages with:

- Page title.
- Data source description.
- One primary action if endpoint is ready.
- Back to homepage link.

- [ ] **Step 2: Keep placeholders honest**

Do not invent fake data. Use loading, empty, or "等待接入" states.

- [ ] **Step 3: Commit**

```bash
git add fit-pet-gym-member-frontend/app/training fit-pet-gym-member-frontend/app/health fit-pet-gym-member-frontend/app/courses fit-pet-gym-member-frontend/app/body fit-pet-gym-member-frontend/app/venue
git commit -m "feat: add safe member feature routes"
```

## Task 7: Polish Global Styling And Copy

**Files:**

- Modify: `fit-pet-gym-member-frontend/app/globals.css`
- Modify: `fit-pet-gym-member-frontend/app/layout.tsx`
- Modify: touched component files with garbled Chinese copy.

- [ ] **Step 1: Replace garbled metadata**

`app/layout.tsx` metadata:

```ts
export const metadata: Metadata = {
  title: "健宠健身房",
  description: "会员健康首页、训练、饮食和课程预约"
};
```

- [ ] **Step 2: Add clean token names**

Keep existing variables where needed, but add clearer aliases:

```css
:root {
  --fit-canvas: #f8f1e0;
  --fit-canvas-deep: #efe1c6;
  --fit-surface: #fff9ec;
  --fit-ink: #20242d;
  --fit-muted: #686b61;
  --fit-mint: #74dfa8;
  --fit-blue: #77c5e8;
  --fit-yolk: #f6c84c;
  --fit-coral: #f48276;
  --fit-pink: #f59bbc;
  --fit-line: #20242d;
  --fit-ease-out: cubic-bezier(0.22, 1, 0.36, 1);
}
```

- [ ] **Step 3: Remove or quarantine old demo styles**

Remove homepage-only demo classes no longer used by the new page, especially gacha, boss, raid, and fake achievement blocks.

- [ ] **Step 4: Commit**

```bash
git add fit-pet-gym-member-frontend/app/globals.css fit-pet-gym-member-frontend/app/layout.tsx fit-pet-gym-member-frontend/app/page.css
git commit -m "style: polish fit-pet member visual system"
```

## Task 8: Optional Character Asset Generation

**Files:**

- Optional create: `fit-pet-gym-member-frontend/public/characters/*.png`
- Optional modify: `fit-pet-gym-member-frontend/components/home/character-stage.tsx`

- [ ] **Step 1: Try coded SVG first**

Do not generate bitmap assets until the coded SVG character is reviewed in browser and judged insufficient.

- [ ] **Step 2: If generation is needed, generate two transparent-style character assets**

Use image generation with this prompt family:

```text
Use case: web app character asset
Asset type: Q-version fitness companion character
Style: warm hand-drawn Chinese fitness app illustration, thick charcoal outline, soft cream paper UI compatibility, restrained cute, not anime glossy, not mobile game explosive
Subject: one full-body fitness girl or boy, standing front-facing, friendly confident pose, simple training outfit
Composition: isolated character, centered, enough negative space, no background text, no logo
Constraints: no watermark, no letters, no extra UI, clean silhouette, consistent line width
```

- [ ] **Step 3: Move final selected assets into project**

Generated project assets must live under `fit-pet-gym-member-frontend/public/characters/`.

- [ ] **Step 4: Commit**

```bash
git add fit-pet-gym-member-frontend/public/characters fit-pet-gym-member-frontend/components/home/character-stage.tsx
git commit -m "feat: add fit-pet character assets"
```

## Task 9: Verification

**Files:**

- No new files required unless tests are added.

- [ ] **Step 1: Build**

Run:

```bash
npm run build
```

Expected:

- Next build completes.
- No TypeScript errors.
- No invalid route imports.

- [ ] **Step 2: Run local dev server**

Run:

```bash
npm run dev
```

Expected:

- App serves on `http://localhost:3010`.

- [ ] **Step 3: Browser QA desktop**

Open `http://localhost:3010`.

Check:

- No blank screen.
- No garbled Chinese on touched routes.
- Homepage redirects to onboarding if needed.
- Completing onboarding returns to homepage.
- Calorie gap and exercise duration render if API succeeds.
- Empty states render if API returns no data.
- Character state changes when model values change.
- Profile avatar routes to `/profile`.

- [ ] **Step 4: Browser QA mobile**

Use a narrow viewport.

Check:

- No horizontal scroll.
- Text does not overlap.
- Tap targets are at least 44px tall.
- Cards and round stat controls do not resize awkwardly.

- [ ] **Step 5: Reduced motion check**

Emulate reduced motion in browser.

Expected:

- Onboarding skips cinematic movement.
- Character loops stop or become minimal.
- Progress and state changes remain understandable.

- [ ] **Step 6: Commit final fixes**

```bash
git add fit-pet-gym-member-frontend
git commit -m "fix: verify fit-pet homepage interactions"
```

## Self Review

Coverage:

- First-login onboarding: Task 3.
- Gender character choice: Task 3 and Task 4.
- Height, weight, age under character: Task 4.
- Today's calorie gap and exercise duration: Task 2 and Task 4.
- Character mood from those two data points: Task 2 and Task 4.
- Five homepage entries: Task 4.
- Profile avatar and personal homepage: Task 4 and Task 5.
- Member card, wallet, equipment repair, chat in profile: Task 5.
- Course booking includes personal training: Task 4 and Task 6.
- Design quality, restrained Q-version style, advanced but gentle motion: Task 1, Task 3, Task 4, Task 7.
- Verification: Task 9.

Risks:

- `/member/profile` response shape should be checked during implementation before wiring wallet/card member id.
- Current frontend may store token under a key other than `token`; if so, `api-client.ts` must align with existing auth storage.
- Existing backend may not expose a combined "today exercise duration" endpoint, so V1 derives it from `/training/logs`.
- If API proxy is not configured for the Next app, `next.config.ts` may need a rewrite to the Spring Boot backend.

No placeholders remain in the implementation tasks. Any optional route or generated asset is explicitly marked optional and has a decision rule.
