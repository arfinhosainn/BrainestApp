-- Create user_weekly_points table
-- This table stores the weekly points schedule for each user
-- Points are randomly generated (2 or 8) once per week and saved to the database

CREATE TABLE IF NOT EXISTS user_weekly_points (
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    week_start_date DATE NOT NULL,
    monday_points INT NOT NULL DEFAULT 2,
    tuesday_points INT NOT NULL DEFAULT 2,
    wednesday_points INT NOT NULL DEFAULT 2,
    thursday_points INT NOT NULL DEFAULT 2,
    friday_points INT NOT NULL DEFAULT 2,
    saturday_points INT NOT NULL DEFAULT 2,
    sunday_points INT NOT NULL DEFAULT 2,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, week_start_date)
);

-- Enable Row Level Security
ALTER TABLE user_weekly_points ENABLE ROW LEVEL SECURITY;

-- Create policy: Users can only see and modify their own weekly points
CREATE POLICY "Users can view their own weekly points"
    ON user_weekly_points
    FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own weekly points"
    ON user_weekly_points
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own weekly points"
    ON user_weekly_points
    FOR UPDATE
    USING (auth.uid() = user_id);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_user_weekly_points_user_id ON user_weekly_points(user_id);
CREATE INDEX IF NOT EXISTS idx_user_weekly_points_week_start_date ON user_weekly_points(week_start_date);

-- Function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update updated_at
CREATE TRIGGER update_user_weekly_points_updated_at
    BEFORE UPDATE ON user_weekly_points
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
