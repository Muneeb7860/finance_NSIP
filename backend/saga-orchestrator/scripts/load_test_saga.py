import time
import json
import uuid
import random
from confluent_kafka import Producer

def delivery_report(err, msg):
    if err is not None:
        print(f'Message delivery failed: {err}')
    else:
        pass # print(f'Message delivered to {msg.topic()} [{msg.partition()}]')

def run_load_test(bootstrap_servers='localhost:9092', message_count=1000):
    p = Producer({'bootstrap.servers': bootstrap_servers})
    
    print(f"🚀 Starting Saga Load Test: {message_count} requests...")
    start_time = time.time()
    
    for i in range(message_count):
        claim_id = str(uuid.uuid4())
        user_id = str(uuid.uuid4())
        
        payload = {
            "claimId": claim_id,
            "userId": user_id,
            "amount": round(random.uniform(100.0, 5000.0), 2),
            "type": "PERSONAL_LOAN",
            "description": f"Load Test Claim #{i}"
        }
        
        p.produce('loan.requested', json.dumps(payload).encode('utf-8'), callback=delivery_report)
        
        if i % 100 == 0:
            p.flush()
            print(f"  ✅ Sent {i} messages...")

    p.flush()
    end_time = time.time()
    duration = end_time - start_time
    
    print("-" * 40)
    print(f"🏁 Load Test Complete!")
    print(f"⏱️ Total Duration: {duration:.2f} seconds")
    print(f"📊 Throughput: {message_count / duration:.2f} msg/sec")
    print("-" * 40)

if __name__ == "__main__":
    run_load_test()
