import os

poms = [
    "api-gateway/pom.xml",
    "auth-service/pom.xml",
    "claim-service/pom.xml",
    "contribution-service/pom.xml",
    "education-service/pom.xml",
    "notification-engine/pom.xml",
    "payment-service/pom.xml",
    "review-service/pom.xml",
    "rewards-service/pom.xml",
    "saga-orchestrator/pom.xml"
]

lombok_dep = """        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
"""

for pom_path in poms:
    full_path = os.path.join("/Users/muneeb/.gemini/antigravity/scratch/nsip/backend", pom_path)
    if os.path.exists(full_path):
        with open(full_path, 'r') as f:
            content = f.read()
        
        if "lombok" not in content:
            new_content = content.replace("    </dependencies>", lombok_dep + "    </dependencies>")
            with open(full_path, 'w') as f:
                f.write(new_content)
            print(f"Updated {pom_path}")
    else:
        print(f"File not found: {pom_path}")
