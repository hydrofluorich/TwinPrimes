;; "Infinite" list of twin prime pairs using Lazy Racket (accurate for primes < ~25 million)
;; Uses deterministic Miller–Rabin test and a difference array
;; Generated 233185 twin prime pairs in 2 minutes for UWaterloo Fall 2021 CS 145 twin primes competition
;; Co-created with Greyson Gould, November 2021

#lang lazy

(define (mr-help-d n r)
  (if (not (zero? (remainder n 2))) (list n r) (mr-help-d (quotient n 2) (add1 r))))

(define (mr-prime? n)
  (define d-r (mr-help-d (sub1 n) 0)) ;; (car d-r) is d, (cadr d-r) is r
  (define d (car d-r))
  (define r (cadr d-r))
  (and (mr-base 2 d r n) (mr-base 3 d r n) (mr-base 5 d r n)) ;; deterministic for primes < ~25 million
  ;(and (mr-base 11000544 d r n) (mr-base 31481107 d r n))
  ;(and (mr-base 2 d r n) (mr-base 3 d r n) (mr-base 5 d r n) (mr-base 7 d r n))
  ;(and (mr-base 2 d r n) (mr-base 7 d r n) (mr-base 61 d r n))
  )

(define (mr-base a d r n)
  (define x (mod-pow a d n))
  ;(define x (modulo (expt a d) n))
  (if (or (= x 1) (= x (sub1 n))) true (mr-recur (remainder (* x x) n) n (sub1 r))))

(define (mr-recur x n count) ;; count starts at r - 1
  (cond
    [(zero? count) false]
    [(= x (sub1 n)) true]
    [true (mr-recur (remainder (* x x) n) n (sub1 count))]))

;; modular exponentiation
(define (mod-pow b e m)
  (if (= m 1) 0 (mod-e 1 (remainder b m) e m)))
(define (mod-e r b e m)
  (cond
    [(zero? e) r]
    [(= (remainder e 2) 1) (mod-e (remainder (* r b) m) (remainder (* b b) m) (quotient e 2) m)]
    [true (mod-e r (remainder (* b b) m) (quotient e 2) m)]))

;; fastest version so far using hashes
(define twinprimes (cons (list 3 5) (cons (list 5 7) (tp-help 12 0))))
(define (tp-help n key)
  (define low (sub1 n))
  (define high (add1 n))
  (if (= 1486 key) (tp-help n 0)
      (if (and (mr-prime? low) (mr-prime? high)) (cons (list low high) (tp-help (+ n (hash-ref adds-h key)) (add1 key))) (tp-help (+ n (hash-ref adds-h key)) (add1 key)))))

;; difference array (hash)
(define adds-h (make-immutable-hash (!! (adds-l 0 3 2))))
(define (adds-l key cur prev)
  (define r5 (remainder cur 5))
  (define r7 (remainder cur 7))
  (define r11 (remainder cur 11))
  (define r13 (remainder cur 13))
  (cond
    [(and (= 2 r5) (= 2 r7) (= 2 r11) (= 2 r13)) (cons (cons key (* 6 (- cur prev))) empty)]
    [(or (= 1 r5) (= 4 r5) (= 1 r7) (= 6 r7) (= 2 r11) (= 9 r11) (= 2 r13) (= 11 r13)) (adds-l key (add1 cur) prev)]
    [true (cons (cons key (* 6 (- cur prev))) (adds-l (add1 key) (add1 cur) cur))]))

;(define t (time (!! (take 100000 twinprimes))))