import csv
import os

files = [
    "./rewards-service/target/site/jacoco/jacoco.csv",
    "./review-service/target/site/jacoco/jacoco.csv",
    "./payment-service/target/site/jacoco/jacoco.csv",
    "./notification-engine/target/site/jacoco/jacoco.csv",
    "./claim-service/target/site/jacoco/jacoco.csv",
    "./contribution-service/target/site/jacoco/jacoco.csv",
    "./saga-orchestrator/target/site/jacoco/jacoco.csv",
    "./event-service/target/site/jacoco/jacoco.csv",
    "./education-service/target/site/jacoco/jacoco.csv",
    "./auth-service/target/site/jacoco/jacoco.csv"
]

total_missed = 0
total_covered = 0

print(f"{'Module':<25} | {'Covered':<10} | {'Missed':<10} | {'%':<5}")
print("-" * 60)

for f in files:
    if not os.path.exists(f):
        continue
    m_covered = 0
    m_missed = 0
    with open(f, mode='r') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            m_missed += int(row['INSTRUCTION_MISSED'])
            m_covered += int(row['INSTRUCTION_COVERED'])
    
    total_missed += m_missed
    total_covered += m_covered
    percentage = (m_covered / (m_covered + m_missed)) * 100 if (m_covered + m_missed) > 0 else 0
    print(f"{f.split('/')[1]:<25} | {m_covered:<10} | {m_missed:<10} | {percentage:.1f}%")

total_percentage = (total_covered / (total_covered + total_missed)) * 100 if (total_covered + total_missed) > 0 else 0
print("-" * 60)
print(f"{'TOTAL':<25} | {total_covered:<10} | {total_missed:<10} | {total_percentage:.1f}%")
