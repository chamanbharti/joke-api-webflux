Request1: curl --location 'http://localhost:8080/jokes?count=5'

Response: 
[
    {
        "id": "4f9bed15-eb0a-47ad-8335-108945ca1c17",
        "question": "Knock-knock.",
        "answer": "A race condition. Who is there?"
    },
    {
        "id": "0b905439-66d9-4325-9071-3dfdcef091cd",
        "question": "What is the hardest part about sky diving?",
        "answer": "The ground."
    },
    {
        "id": "8b4fc4f0-a578-4f85-93c8-1f2eaae9941f",
        "question": "How many hipsters does it take to change a lightbulb?",
        "answer": "Oh, it's a really obscure number. You've probably never heard of it."
    },
    {
        "id": "10b1766a-5981-467d-bfc9-1ba68a45843c",
        "question": "Why did the scarecrow win an award?",
        "answer": "Because he was outstanding in his field."
    },
    {
        "id": "4be3c581-21ac-40c6-86d1-1a5184efdead",
        "question": "What's Forrest Gump's password?",
        "answer": "1Forrest1"
    }
]

Request2: curl --location 'http://localhost:8080/jokes?count=0'
Response: 
Count must be between 1 and 100

Request3: curl --location 'http://localhost:8080/jokes?count=150'
Response: 
Count must be between 1 and 100

