# Entity Relationship Diagram (ERD)

This diagram outlines the relational database schema (PostgreSQL) for the National Social Insurance Platform, updated with the new LMS and Gamification entities.

```mermaid
erDiagram
    %% Core Entities
    USERS {
        uuid id PK
        string national_id UK
        string full_name
        string email
        string role
    }

    EMPLOYMENTS {
        uuid id PK
        uuid user_id FK
        decimal current_salary
        string status
    }

    CLAIMS {
        uuid id PK
        uuid user_id FK
        string claim_type
        string status
    }

    %% LMS Entities (New)
    COURSES {
        uuid id PK
        string title
        string category "Save, Invest, Multiply"
        int total_points_reward
    }

    VIDEOS {
        uuid id PK
        uuid course_id FK
        string title
        string embedded_url "YouTube/Vimeo Link"
        int points_reward
    }

    USER_PROGRESS {
        uuid id PK
        uuid user_id FK
        uuid video_id FK
        boolean is_completed
        int interactive_score "Gamification quiz score"
        datetime completed_at
    }

    CERTIFICATES {
        uuid id PK
        uuid user_id FK
        uuid course_id FK
        string certificate_url
    }

    %% Event Entities (New)
    EVENTS {
        uuid id PK
        string title
        string type "Digital, Physical"
        int attendance_points_reward
        datetime start_time
    }

    EVENT_ATTENDEES {
        uuid id PK
        uuid event_id FK
        uuid user_id FK
        boolean has_attended
    }

    %% Gamification & Rewards Entities (New)
    POINTS_LEDGER {
        uuid id PK
        uuid user_id FK
        int point_delta "e.g., +50 for video, -1000 for session"
        string description
        datetime created_at
    }

    ADVISOR_SESSIONS {
        uuid id PK
        uuid user_id FK
        uuid advisor_id FK
        datetime scheduled_time
        string status "Scheduled, Canceled, Rescheduled, Completed"
    }

    %% Relations
    USERS ||--o{ USER_PROGRESS : "tracks"
    COURSES ||--o{ VIDEOS : "contains"
    VIDEOS ||--o{ USER_PROGRESS : "has"
    USERS ||--o{ CERTIFICATES : "earns"
    COURSES ||--o{ CERTIFICATES : "grants"
    
    EVENTS ||--o{ EVENT_ATTENDEES : "hosts"
    USERS ||--o{ EVENT_ATTENDEES : "RSVPs to"
    
    USERS ||--o{ POINTS_LEDGER : "has balance in"
    USERS ||--o{ ADVISOR_SESSIONS : "books"
    USERS ||--o{ ADVISOR_SESSIONS : "hosts (advisor)"
```
