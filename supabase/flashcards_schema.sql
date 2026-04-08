-- Flashcards schema + RLS policies for Supabase

create table if not exists public.decks (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    title text not null,
    source_filename text,
    total_cards integer not null default 0,
    created_at timestamptz not null default now()
);

create table if not exists public.flashcards (
    id uuid primary key default gen_random_uuid(),
    deck_id uuid not null references public.decks(id) on delete cascade,
    front text not null,
    back text not null,
    order_index integer not null,
    created_at timestamptz not null default now()
);

create table if not exists public.study_sessions (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    deck_id uuid not null references public.decks(id) on delete cascade,
    cards_known integer not null default 0,
    cards_unknown integer not null default 0,
    total_swiped integer not null default 0,
    started_at timestamptz not null default now(),
    ended_at timestamptz
);

create table if not exists public.session_records (
    id uuid primary key default gen_random_uuid(),
    session_id uuid not null references public.study_sessions(id) on delete cascade,
    flashcard_id uuid not null references public.flashcards(id) on delete cascade,
    result text not null check (result in ('known', 'unknown')),
    responded_at timestamptz not null default now()
);

create table if not exists public.study_sources (
    id uuid primary key default gen_random_uuid(),
    deck_id uuid not null references public.decks(id) on delete cascade,
    source_type text not null check (source_type in ('document', 'audio')),
    source_text text,
    source_file_id text,
    source_filename text,
    smart_notes text,
    created_at timestamptz not null default now()
);

alter table public.study_sources
add column if not exists smart_notes text;

create table if not exists public.quiz_questions (
    id uuid primary key default gen_random_uuid(),
    deck_id uuid not null references public.decks(id) on delete cascade,
    question text not null,
    options jsonb not null,
    correct_index integer not null,
    order_index integer not null,
    created_at timestamptz not null default now()
);

create index if not exists decks_user_id_idx on public.decks (user_id);
create index if not exists flashcards_deck_id_idx on public.flashcards (deck_id);
create index if not exists study_sessions_user_id_idx on public.study_sessions (user_id);
create index if not exists study_sessions_deck_id_idx on public.study_sessions (deck_id);
create index if not exists session_records_session_id_idx on public.session_records (session_id);
create index if not exists session_records_flashcard_id_idx on public.session_records (flashcard_id);
create index if not exists study_sources_deck_id_idx on public.study_sources (deck_id);
create index if not exists quiz_questions_deck_id_idx on public.quiz_questions (deck_id);

alter table public.decks enable row level security;
alter table public.flashcards enable row level security;
alter table public.study_sessions enable row level security;
alter table public.session_records enable row level security;
alter table public.study_sources enable row level security;
alter table public.quiz_questions enable row level security;

create policy "Decks are owned by user" on public.decks
for all
using (auth.uid() = user_id)
with check (auth.uid() = user_id);

create policy "Flashcards are owned by deck owner" on public.flashcards
for all
using (
    exists (
        select 1
        from public.decks d
        where d.id = deck_id
          and d.user_id = auth.uid()
    )
)
with check (
    exists (
        select 1
        from public.decks d
        where d.id = deck_id
          and d.user_id = auth.uid()
    )
);

create policy "Sessions are owned by user" on public.study_sessions
for all
using (auth.uid() = user_id)
with check (auth.uid() = user_id);

create policy "Session records are owned by session owner" on public.session_records
for all
using (
    exists (
        select 1
        from public.study_sessions s
        where s.id = session_id
          and s.user_id = auth.uid()
    )
)
with check (
    exists (
        select 1
        from public.study_sessions s
        where s.id = session_id
          and s.user_id = auth.uid()
    )
);

create policy "Study sources are owned by deck owner" on public.study_sources
for all
using (
    exists (
        select 1
        from public.decks d
        where d.id = deck_id
          and d.user_id = auth.uid()
    )
)
with check (
    exists (
        select 1
        from public.decks d
        where d.id = deck_id
          and d.user_id = auth.uid()
    )
);

create policy "Quiz questions are owned by deck owner" on public.quiz_questions
for all
using (
    exists (
        select 1
        from public.decks d
        where d.id = deck_id
          and d.user_id = auth.uid()
    )
)
with check (
    exists (
        select 1
        from public.decks d
        where d.id = deck_id
          and d.user_id = auth.uid()
    )
);
