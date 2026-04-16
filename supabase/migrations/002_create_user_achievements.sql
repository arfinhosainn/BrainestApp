-- Create user_achievements table
-- Stores fast-access, denormalized achievement totals per user.

CREATE TABLE IF NOT EXISTS user_achievements (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    total_points INT NOT NULL DEFAULT 0,
    current_streak_days INT NOT NULL DEFAULT 0,
    longest_streak_days INT NOT NULL DEFAULT 0,
    completed_decks_count INT NOT NULL DEFAULT 0,
    completed_quizzes_count INT NOT NULL DEFAULT 0,
    last_activity_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Optional event log to track achievement updates over time.
CREATE TABLE IF NOT EXISTS user_achievement_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    event_type TEXT NOT NULL CHECK (
        event_type IN ('POINTS_EARNED', 'STREAK_UPDATED', 'DECK_COMPLETED', 'QUIZ_COMPLETED')
    ),
    points_delta INT,
    related_deck_id UUID REFERENCES public.decks(id) ON DELETE SET NULL,
    occurred_on DATE,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE user_achievements ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_achievement_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own achievements"
    ON user_achievements
    FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own achievements"
    ON user_achievements
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own achievements"
    ON user_achievements
    FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Users can view their own achievement events"
    ON user_achievement_events
    FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own achievement events"
    ON user_achievement_events
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_user_achievement_events_user_id
    ON user_achievement_events(user_id);

CREATE INDEX IF NOT EXISTS idx_user_achievement_events_created_at
    ON user_achievement_events(created_at DESC);

-- Reuse existing trigger function from prior migrations.
CREATE TRIGGER update_user_achievements_updated_at
    BEFORE UPDATE ON user_achievements
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
