/*
 * The MIT License (MIT)
 *
 * Copyright (c)  2020. Nikolai Kotchetkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import {Database, Statement} from "sqlite3";
import {Category} from "./data/Expense";

/**
 * Populates DB with data
 */
export async function populate(db: Database, Categories: Array<Category>): Promise<void> {
    console.log("Populating expense tracker database...");
    await populateCategories(db, Categories);
}

/**
 * Sample data validation
 * @param category
 */
function isValidData(category: Category): Boolean {
    return category.name.length > 0 && category.id > 0;
}

/**
 * Populates Categories
 */
 async function populateCategories(db: Database, categories: Array<Category>) {
    console.log("Populating Categories table...");
    let validRecords = 0;
    let invalidRecords: Array<number> = [];

    db.serialize();
    await new Promise<void> (((resolve, reject) => {
        db.run(
            "begin transaction",
            function(err: Error) {
                if(null != err) {
                    reject(err);
                } else {
                    resolve();
                }
            }
        )
    }));
    const stmt = await new Promise<Statement> ((resolve, reject) => {
        db.prepare(
            "REPLACE INTO category VALUES (?,?)",
            function(this: Statement, err: Error) {
                if(null != err) {
                    reject(err);
                } else {
                    resolve(this);
                }
            }
        );
    });

    for (const category of categories) {
        if (false == isValidData(category)) {
            console.warn(`Invalid category: ${category.id}`);
            invalidRecords.push(category.id);
            continue;
        }
        await new Promise<void>((resolve, reject) => {
            stmt.run(
                [
                    category.name,
                    category.id
                   
                ],
                function (err: Error) {
                    if (null != err) {
                        reject(err);
                    } else {
                        validRecords++;
                        resolve();
                    }
                }
            );
        })
    }
    console.log("Categories populated.");
    console.log(`Valid records: ${validRecords}`);
    console.log(`Invalid records (${invalidRecords.length}):`, invalidRecords);
    await new Promise<void> (((resolve, reject) => {
        stmt.finalize(
            function(err: Error) {
                if(null != err) {
                    reject(err);
                } else {
                    resolve();
                }
            }
        )
    }));
    await new Promise<void> (((resolve, reject) => {
        db.run(
            "commit",
            function(err: Error) {
                if(null != err) {
                    reject(err);
                } else {
                    resolve();
                }
            }
        )
    }));
}

