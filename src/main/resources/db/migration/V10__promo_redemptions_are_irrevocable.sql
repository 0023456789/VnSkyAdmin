-- A redeemed promo remains consumed after its subscription is cancelled.
-- Enforce the per-MSISDN limit in the subscription transaction, where requests
-- are serialized by the MSISDN advisory lock. Drop the old partial unique index
-- because historical data may already contain redemptions followed by cancel/redeem.
DROP INDEX IF EXISTS uq_sub_single_use_promo;
