import http from 'k6/http';
import { check } from 'k6';

export const options = {
    discardResponseBodies: true, // экономия памяти

    scenarios: {
        write_1000_rps: {
            executor: 'constant-arrival-rate',
            rate: 4000,                // 1000 запросов в секунду
            timeUnit: '1s',
            duration: '1m',            // продолжительность теста
            preAllocatedVUs: 100,      // начальное количество VU
            maxVUs: 300,               // максимум VU (для поддержки 1000 RPS)
        },
    },

    thresholds: {
        //  Провалить тест, если доля запросов с ошибкой 5xx > 0%
        'http_req_failed': ['rate == 0']/*,

        // Дополнительно: контроль времени ответа (опционально)
        'http_req_duration': ['p(95)<500'], // 95% запросов быстрее 500 мс*/
    },
};

export default function () {
    // Генерируем уникальные данные для записи
    const payload = {
        "walletId": "e9315e3d-c2f5-4b73-833a-1430472cdb74",
        "operationType": "DEPOSIT",
        "amount": 1
    };

    const res = http.post('http://localhost:8080/api/v1/wallet', JSON.stringify(payload), {
        headers: { 'Content-Type': 'application/json' },
    });

    // Явная проверка на статус 2xx (201/200) и отсутствие 5xx
    const isSuccess = check(res, {
        'status is 2xx': (r) => r.status >= 200 && r.status < 300,
        'no 5xx error': (r) => r.status < 500,
    });

    // Если check упал (а это значит, что статус не 2xx или 5xx) — можно дополнительно логировать
    if (!isSuccess) {
        //console.error(`Failed request: status ${res.status}, body: ${res.body.substring(0, 200)}`);
    }
}